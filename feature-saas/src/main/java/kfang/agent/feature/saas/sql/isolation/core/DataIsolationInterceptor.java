package kfang.agent.feature.saas.sql.isolation.core;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.instance.ReflectionUtil;
import cn.hyugatool.core.string.StringUtil;
import kfang.agent.feature.saas.exception.DataIsolationException;
import kfang.agent.feature.saas.sql.isolation.annotation.DataIsolationDao;
import kfang.agent.feature.saas.sql.isolation.annotation.SkipDataIsolation;
import kfang.agent.feature.saas.sql.print.SqlPrintInterceptor;
import kfang.infra.common.isolation.DataIsolation;
import kfang.infra.common.isolation.PlatformDataIsolation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.type.StringTypeHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TestInterceptor
 *
 * @author pengqinglong
 * @since 2021/12/15
 */
@Component
@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class DataIsolationInterceptor implements Interceptor {

    private static final String AND = "and";
    private static final String SQL = "sql";
    private static final String BLANK = " ";
    private static final String JOIN = "JOIN";
    private static final String WHERE = "where";
    private static final String TARGET = "target";

    private SqlPrintInterceptor sqlPrintInterceptor;

    /**
     * 代理缓存
     */
    private final Map<Object, MappedStatement> proxyCache = new ConcurrentHashMap<>();

    /**
     * 需要跳过的服务集合
     */
    private static final List<String> SKIP_SERVICES = ListUtil.newArrayList();

    static {
        // SERVICE-AGENT-JMS
        SKIP_SERVICES.add("service-scheduler");
        // SERVICE-AGENT-DTS
        SKIP_SERVICES.add("service-dts");
        // WEB-AGENT_THIRDPARTY
        SKIP_SERVICES.add("openapi");
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();

        // 如果没有 那么进行初始化
        MappedStatement mappedStatement = this.getCacheMappedStatement(handler);

        // insert不处理 交由mysql字段必填处理
        if (SqlCommandType.INSERT == mappedStatement.getSqlCommandType()) {
            return invocation.proceed();
        }

        // 获取执行id
        String id = mappedStatement.getId();
        int index = id.lastIndexOf(".");

        // 获取class
        String className = id.substring(0, index);
        Class<?> daoClass = Class.forName(className);

        // 获取方法
        Method method = this.getMethod(id, index, daoClass);

        // 如果实现数据隔离接口 找是否有不参与数据隔离注解
        SkipDataIsolation annotation = method.getAnnotation(SkipDataIsolation.class);

        // 如果无数据隔离注解 或者全部跳过 直接执行
        boolean methodNoIsolationDaoAnnotation = method.getAnnotation(DataIsolationDao.class) == null;
        boolean daoClassNoIsolationDaoAnnotation = daoClass.getAnnotation(DataIsolationDao.class) == null;
        boolean skipDataIsolation = annotation.level() == SkipDataIsolation.Level.ALL;
        boolean theQuarantineConditionIsNotMet = (methodNoIsolationDaoAnnotation && daoClassNoIsolationDaoAnnotation) || skipDataIsolation;
        if (theQuarantineConditionIsNotMet) {
            // 不满足隔离条件
            return invocation.proceed();
        }

        // 判断入参是否实现了接口
        ParameterHandler parameterHandler = handler.getParameterHandler();
        Object parameterObject = parameterHandler.getParameterObject();
        Class<?> paramClass = parameterObject.getClass();

        // 如果参数没有实现接口 在开发阶段就抛出异常
        if (!DataIsolation.class.isAssignableFrom(paramClass)) {
            throw new DataIsolationException(String.format("%s 参数未实现 DataIsolation 接口", id));
        }

        // 无需隔离服务 跳过隔离 无法获取隔离参数
        DataIsolation dataIsolation = (DataIsolation) parameterObject;
        if (this.checkSkipService(dataIsolation)) {
            this.removeCacheDevSqlPrint(mappedStatement);
            return invocation.proceed();
        }

        // 准备隔离集合
        List<SkipDataIsolation.Level> sqlList = this.prepareIsolation(paramClass, annotation);

        // 如果无需要组装的sql参数 则直接执行
        if (sqlList.isEmpty()) {
            return invocation.proceed();
        }

        // 获取sql和预编译参数
        BoundSql boundSql = handler.getBoundSql();
        String sql = boundSql.getSql();

        // 判断sql是否存在表关联 存在表关联只检查sql中是否有对应的查询参数
        String upperCase = sql.toUpperCase();
        if (upperCase.contains(JOIN)) {
            upperCase = upperCase.replaceAll(BLANK, "");
            for (SkipDataIsolation.Level next : sqlList) {
                boolean contains = upperCase.contains(String.format("%s=?", next.getSqlFieldName()));
                if (!contains) {
                    throw new DataIsolationException(String.format("%s 数据隔离sql 没有 [%s] 参数", id, next.getSqlFieldName()));
                }
            }
            return invocation.proceed();
        }

        // 开始组装
        this.handleSql(mappedStatement, sqlList, boundSql);

        // 缓存开发环境的sql打印
        this.cacheDevSqlPrint(mappedStatement, boundSql);

        return invocation.proceed();
    }

    /**
     * 校验是否是跳过隔离的服务来源
     */
    private boolean checkSkipService(DataIsolation dataIsolation) {
        return SKIP_SERVICES.contains(dataIsolation.getOperatorSystem());
    }

    /**
     * 组装单表sql
     */
    private void handleSql(MappedStatement mappedStatement, List<SkipDataIsolation.Level> sqlList, BoundSql boundSql) {

        // 获取sql和预编译参数
        String sql = boundSql.getSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        // 找到where位置
        int whereIndex = sql.toLowerCase().lastIndexOf(WHERE);

        // sql组装
        Iterator<SkipDataIsolation.Level> iterator = sqlList.iterator();
        StringBuilder sb = new StringBuilder(sql.substring(0, whereIndex + 5));
        while (iterator.hasNext()) {
            SkipDataIsolation.Level next = iterator.next();

            // 预编译模式 参数值为?
            sb.append(String.format(" %s = ? and", next.getSqlFieldName()));

            // 生成预编译参数 放入paramMappings中
            ParameterMapping build = new ParameterMapping.Builder(mappedStatement.getConfiguration(), next.getJavaFieldName(), new StringTypeHandler()).build();
            parameterMappings.add(next.ordinal() - 1, build);
        }
        String head = sb.toString();
        String tail = sql.substring(whereIndex + 5);

        // 如果tail中还有and 将head结尾的and干掉
        if (tail.trim().toLowerCase().startsWith(AND)) {
            head = StringUtil.removeEnd(head, AND);
        }

        // 去除结尾的and
        String newSql = head + tail;

        // sql重新赋值
        ReflectionUtil.setFieldValue(boundSql, SQL, newSql);
    }

    /**
     * 缓存开发环境的sql打印
     */
    private void cacheDevSqlPrint(MappedStatement mappedStatement, BoundSql boundSql) {
        if (sqlPrintInterceptor != null) {
            if (!sqlPrintInterceptor.containsSql(mappedStatement.getId())) {
                sqlPrintInterceptor.putSqlMapping(mappedStatement.getId(), boundSql);
            }
        }
    }

    /**
     * 清除开发环境的sql打印缓存
     */
    private void removeCacheDevSqlPrint(MappedStatement mappedStatement) {
        if (sqlPrintInterceptor != null) {
            if (sqlPrintInterceptor.containsSql(mappedStatement.getId())) {
                sqlPrintInterceptor.removeSql(mappedStatement.getId());
            }
        }
    }

    /**
     * 准备好隔离集合
     */
    private List<SkipDataIsolation.Level> prepareIsolation(Class<?> paramClass, SkipDataIsolation annotation) {
        // 保存预插入的sql体
        List<SkipDataIsolation.Level> sqlList = ListUtil.newArrayList();

        if (PlatformDataIsolation.class.isAssignableFrom(paramClass)) {
            // 无注解 或者 注解跳过的隔离级别大于platform 则需要处理
            if (annotation == null || annotation.level().ordinal() < SkipDataIsolation.Level.PLATFORM.ordinal()) {
                sqlList.add(SkipDataIsolation.Level.PLATFORM);
            }
        }

        return sqlList;
    }

    /**
     * 获取方法
     */
    private Method getMethod(String id, int index, Class<?> daoClass) {
        // 截取方法名
        String methodName = id.substring(index + 1);

        // mybatis不允许方法重载 所以这里通过方法名肯定只有一个
        Method[] declaredMethods = daoClass.getDeclaredMethods();
        return Arrays.stream(declaredMethods)
                .filter(methodTemp -> Objects.equals(methodTemp.getName(), methodName))
                .findFirst()
                .orElse(null);
    }

    /**
     * 从缓存中拿取MappedStatement
     */
    private MappedStatement getCacheMappedStatement(StatementHandler handler) throws Throwable {

        // 从缓存中拿取mappedStatement
        MappedStatement mappedStatement = proxyCache.get(handler);

        if (mappedStatement == null) {
            Field field = handler.getClass().getSuperclass().getDeclaredField("h");
            Object target = null;
            while (true) {
                try {
                    field.setAccessible(true);
                    Plugin p = (Plugin) field.get(target == null ? handler : target);
                    target = ReflectionUtil.getFieldValue(p, TARGET);
                    field = target.getClass().getSuperclass().getDeclaredField("h");
                } catch (NoSuchFieldException e) {
                    break;
                }
            }

            MetaObject metaObject = MetaObject.forObject(target, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());

            mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
            proxyCache.put(handler, mappedStatement);
        }
        return mappedStatement;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

}