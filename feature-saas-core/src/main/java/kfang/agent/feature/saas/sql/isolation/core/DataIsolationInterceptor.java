package kfang.agent.feature.saas.sql.isolation.core;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.instance.ReflectionUtil;
import cn.hyugatool.core.string.StringUtil;
import kfang.agent.feature.saas.logger.LogModule;
import kfang.agent.feature.saas.logger.util.LogUtil;
import kfang.agent.feature.saas.sql.isolation.annotation.DataIsolationDao;
import kfang.agent.feature.saas.sql.isolation.annotation.SkipDataIsolation;
import kfang.agent.feature.saas.sql.isolation.enums.Level;
import kfang.agent.feature.saas.sql.isolation.exception.DataIsolationException;
import kfang.agent.feature.saas.sql.print.SqlPrintInterceptor;
import kfang.infra.common.isolation.DataIsolation;
import lombok.Setter;
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
import java.nio.charset.StandardCharsets;
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
public class DataIsolationInterceptor implements Interceptor{

    private static final String AND = "and";
    private static final String SQL = "sql";
    private static final String BLANK = " ";
    private static final String JOIN = "JOIN";
    private static final String WHERE = "where";
    private static final String TARGET = "target";
    public static final String CHECK_SQL_STR = "%s=?";
    public static final String SET = "set";

    @Setter
    private SqlPrintInterceptor sqlPrintInterceptor;

    /**
     * 代理缓存
     */
    private final Map<Object, MappedStatement> proxyCache = new ConcurrentHashMap<>();

    /**
     * 需要跳过的服务集合
     */
    private static final List<String> SKIP_SERVICES = ListUtil.newArrayList();

    /**
     * 预编译参数 放入paramMappings中
     */
    private final Map<Level, ParameterMapping> paramMappings = new ConcurrentHashMap<>();

    static {
        // SERVICE-AGENT-JMS
        SKIP_SERVICES.add("SERVICE_SCHEDULER");
        // SERVICE-AGENT-DTS
        SKIP_SERVICES.add("SERVICE_DTS");
        // WEB-AGENT_THIRDPARTY
        SKIP_SERVICES.add("WEB_AGENT_THIRDPARTY");
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

        DataIsolationDao methodAnnotation = method.getAnnotation(DataIsolationDao.class);
        DataIsolationDao classAnnotation = daoClass.getAnnotation(DataIsolationDao.class);
        // 如果无数据隔离注解 或者全部跳过 直接执行
        boolean methodNoIsolationDaoAnnotation = methodAnnotation == null;
        boolean daoClassNoIsolationDaoAnnotation = classAnnotation == null;
        boolean skipDataIsolation = annotation != null && annotation.level() == Level.ALL;
        boolean theQuarantineConditionIsNotMet = (methodNoIsolationDaoAnnotation && daoClassNoIsolationDaoAnnotation) || skipDataIsolation;
        if (theQuarantineConditionIsNotMet) {
            // 不满足隔离条件
            return invocation.proceed();
        }

        // 获取最终使用的注解
        DataIsolationDao dataIsolationAnnotation = daoClassNoIsolationDaoAnnotation ? null : classAnnotation;
        // method会覆盖掉dao层的注解 单方法的最为优先
        dataIsolationAnnotation = methodNoIsolationDaoAnnotation ? dataIsolationAnnotation : methodAnnotation;

        // 判断入参是否实现了接口
        ParameterHandler parameterHandler = handler.getParameterHandler();
        Object parameterObject = parameterHandler.getParameterObject();
        Class<?> paramClass = parameterObject.getClass();

        // 如果参数没有实现接口 在开发阶段就抛出异常
        if (!DataIsolation.class.isAssignableFrom(paramClass)) {
            throw new DataIsolationException(" 参数未实现 DataIsolation 接口");
        }

        // 获取sql和预编译参数
        BoundSql boundSql = handler.getBoundSql();

        // 无需隔离服务 跳过隔离 无法获取隔离参数
        DataIsolation dataIsolation = (DataIsolation) parameterObject;
        if (this.checkSkipService(dataIsolation)) {

            List<ParameterMapping> oldParamList = boundSql.getParameterMappings();
            // 跳过隔离的话将隔离参数重新构建并且remove隔离参数
            List<ParameterMapping> nowParamList = ListUtil.newArrayList();
            nowParamList.addAll(oldParamList);
            nowParamList.removeAll(paramMappings.values());
            ReflectionUtil.setFieldValue(boundSql, "parameterMappings", nowParamList);

            this.removeCacheDevSqlPrint(mappedStatement);
            return invocation.proceed();
        }

        // 提前优化点
        String sql = boundSql.getSql();
        String sqlUpperCase = sql.toUpperCase().replaceAll(BLANK, "");

        // 准备隔离集合
        try {
            List<Level> sqlList = this.prepareIsolation(dataIsolationAnnotation, sqlUpperCase, paramClass, annotation);

            // 如果无需要组装的sql参数 则直接执行
            if (sqlList.isEmpty()) {
                return invocation.proceed();
            }

            // update 需要检查一下 update不允许更新数据隔离的字段
            if(SqlCommandType.UPDATE == mappedStatement.getSqlCommandType()){
                this.checkUpdateSql(sqlUpperCase, sqlList);
            }

            // 判断sql是否存在表关联 存在表关联只检查sql中是否有对应的查询参数
            if (sqlUpperCase.contains(JOIN)) {
                for (Level level : sqlList) {
                    if (!checkSqlLevelField(sqlUpperCase, level)) {
                        throw new DataIsolationException(String.format(" 数据隔离sql 没有 [%s] 参数", level.getSqlFieldName()));
                    }
                }
                return invocation.proceed();
            }

            // 判断字段是否有值
            for (Level level : sqlList) {
                // 需要拼接sql
                if (level.isJoinSql()) {
                    String javaFieldName = level.getJavaFieldName();
                    String methodName = "get" + javaFieldName.substring(0, 1).toUpperCase() + javaFieldName.substring(1);
                    Object fieldValue = ReflectionUtil.invokeMethod(parameterObject, methodName);
                    if(StringUtil.isEmpty(fieldValue)){
                        throw new DataIsolationException(String.format(" 数据隔离sql 没有 [%s] 参数", level.getSqlFieldName()));
                    }
                }
            }

            // 开始组装
            this.handleSql(mappedStatement, sqlList, boundSql);

            // 缓存开发环境的sql打印
            this.cacheDevSqlPrint(mappedStatement, boundSql);

        }catch (DataIsolationException e){
            // 重新组织错误信息 补充id至错误信息中
            LogUtil.error(log, "数据隔离", "隔离处理异常",e);
            throw new DataIsolationException(id + e.getMessage());
        }catch (Exception e){
            LogUtil.error(log, "数据隔离", "隔离处理异常",e);
            throw e;
        }
        return invocation.proceed();

    }

    /**
     * 检查update的Sql
     */
    private void checkUpdateSql(String sqlUpperCase, List<Level> sqlList) {
        // 截取从第一个set字符串到第一个where中间
        String substring = sqlUpperCase.substring(sqlUpperCase.indexOf(SET.toUpperCase()), sqlUpperCase.indexOf(WHERE.toUpperCase()));

        for (Level level : sqlList) {
            // 如果set中存在隔离参数 抛出异常 不允许更新
            if (substring.contains(level.getSqlFieldName())) {
                throw new DataIsolationException(String.format(" 数据隔离sql update中更新到了 [%s] 隔离参数", level.getSqlFieldName()));
            }
        }
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
    private void handleSql(MappedStatement mappedStatement, List<Level> sqlList, BoundSql boundSql) {

        // 获取sql和预编译参数
        String sql = boundSql.getSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        // 找到where位置
        int whereIndex = sql.toLowerCase().lastIndexOf(WHERE);

        // sql组装
        Iterator<Level> iterator = sqlList.iterator();
        StringBuilder sb = new StringBuilder(sql.substring(0, this.getTailIndex(sql, whereIndex)));
        // 如果sql无where 则补充一个where
        if(!this.hasWhere(whereIndex)){
            sb.append(BLANK + WHERE +BLANK);
        }

        while (iterator.hasNext()) {
            Level next = iterator.next();

            if (!next.isJoinSql()) {
                continue;
            }

            // 预编译模式 参数值为?
            sb.append(String.format(" %s = ? and", next.getSqlFieldName()));

            // 本地缓存预编译参数未生成 生成预编译参数 放入paramMappings中
            if(!paramMappings.containsKey(next)){
                // 双重检查锁
                synchronized (paramMappings){
                    if(!paramMappings.containsKey(next)){
                        // 构建一个param对象 存入缓存
                        ParameterMapping param = new ParameterMapping.Builder(mappedStatement.getConfiguration(), next.getJavaFieldName(), new StringTypeHandler())
                                .javaType(String.class)
                                .build();
                        paramMappings.put(next, param);
                        // 初始化进去
                        this.addParam(mappedStatement.getSqlCommandType(), boundSql, parameterMappings, whereIndex, next);

                    }
                }
            }else{
                ParameterMapping param = paramMappings.get(next);
                if (!parameterMappings.contains(param)) {
                    // 双重检查锁 这里锁对象使用mybatis的parameterMappings对象
                    // 此对象经过观察 在boundSql对象中有复用的情况 所以加锁 且锁粒度细化
                    synchronized (parameterMappings){
                        if(!parameterMappings.contains(param)){
                            this.addParam(mappedStatement.getSqlCommandType(), boundSql, parameterMappings, whereIndex, next);
                        }
                    }
                }
            }
        }
        String head = sb.toString();
        String tail = sql.substring(getTailIndex(sql, whereIndex));

        // 如果tail中还有and 将head结尾的and干掉
        if (tail.trim().toLowerCase().startsWith(AND) || !hasWhere(whereIndex)) {
            head = StringUtil.removeEnd(head, AND);
        }

        // 去除结尾的and
        String newSql = head + tail;

        // sql重新赋值
        ReflectionUtil.setFieldValue(boundSql, SQL, newSql);
    }

    /**
     * 为sql对象添加隔离参数
     */
    private void addParam(SqlCommandType sqlCommandType, BoundSql boundSql, List<ParameterMapping> parameterMappings, int whereIndex, Level next) {

        // 默认为当前隔离级别 -1
        int paramIndex = next.ordinal() - 1;

        // 如果是update 则要算出where之前的update有几个参数
        String sql = boundSql.getSql().toLowerCase();
        String set = sql.substring(0, this.hasWhere(whereIndex) ? whereIndex : sql.length());

        byte[] bytes = set.getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == '?') {
                paramIndex++;
            }
        }

        if(ListUtil.isEmpty(parameterMappings)){
            List<ParameterMapping> nowParamList = ListUtil.newArrayList();
            nowParamList.addAll(parameterMappings);
            nowParamList.add(paramIndex, paramMappings.get(next));
            ReflectionUtil.setFieldValue(boundSql, "parameterMappings", nowParamList);
        }else {
            parameterMappings.add(paramIndex, paramMappings.get(next));
        }
    }

    /**
     * 获取sql以where分割后的尾部起始下标
     */
    private int getTailIndex(String sql, int whereIndex) {
        // 判断where是否存在 如果where不存在 则找出应该插入where的位子
        return this.hasWhere(whereIndex) ? whereIndex + 5 : this.getInsertWhereIndex(sql);
    }

    /**
     * 找出sql插入where的位子
     */
    private int getInsertWhereIndex(String sql){
        int from = sql.indexOf(" FROM ") + 6;
        sql = sql.substring(from);
        byte[] bytes = sql.getBytes(StandardCharsets.UTF_8);

        boolean flag = false;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == '.') {
                flag = true;
            }
            if(flag && bytes[i] == ' '){
                return from + i;
            }
        }
        return sql.length();
    }

    /**
     * sql中是否存在where
     */
    private boolean hasWhere(int whereIndex) {
        return !(whereIndex == -1);
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
    private List<Level> prepareIsolation(DataIsolationDao dataIsolationAnnotation, String sqlUpperCase, Class<?> paramClass, SkipDataIsolation annotation) {
        // 保存预插入的sql体
        List<Level> sqlList = ListUtil.newArrayList();

        // 当前需要隔离的级别
        Level level = dataIsolationAnnotation.level();

        // 隔离级别循环
        for (Level e : Level.values()) {
            // ALL作为最顶层不参与隔离业务逻辑处理
            if(e == Level.ALL){
                continue;
            }
            // 小于等于当前隔离级别的都应该被隔离
            if(level.ordinal() >= e.ordinal()){
                this.addSqlIsolationLevel(sqlUpperCase, annotation, sqlList, e);

                // 是否需要拼接sql判断 如果需要拼接sql则需要实现对应的form
                if (e.isJoinSql()) {
                    Class<? extends DataIsolation> clazz = e.getClazz();
                    if (!clazz.isAssignableFrom(paramClass)) {
                        throw new DataIsolationException(" 参数未实现 DataIsolation 接口");
                    }
                }
            }
        }

        return sqlList;
    }

    /**
     * 添加sql隔离级别
     */
    private void addSqlIsolationLevel(String sqlUpperCase, SkipDataIsolation annotation, List<Level> sqlList, Level level) {
        // 无注解 或者 注解跳过的隔离级别大于当前级别 则需要处理
        if (annotation == null || annotation.level().ordinal() < level.ordinal()) {
            // 提前优化点 如果sql中存在参数 就不用添加了 如果sql所有的隔离点都加好了 那么直接执行sql 不用做sql替换的处理了
            if(!checkSqlLevelField(sqlUpperCase, level)){
                sqlList.add(level);
            }
        }
    }

    /**
     * 检查sql中是否存在字段
     */
    private boolean checkSqlLevelField(String sqlUpperCase, Level level) {
        boolean contains = sqlUpperCase.contains(String.format(CHECK_SQL_STR, level.getSqlFieldName()));

        // 无需拼接的隔离级别如果sql没有自带字段 则直接抛出异常
        if (!contains && !level.isJoinSql()) {
            throw new DataIsolationException(String.format(" 数据隔离sql 没有 [%s] 参数", level.getSqlFieldName()));
        }

        return contains;
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

        // 通过sql的字符串常量作为缓存key唯一标示
        String key = handler.getBoundSql().getSql().intern();

        // 从缓存中获取MappedStatement
        MappedStatement mappedStatement = proxyCache.get(key);

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
            proxyCache.put(key, mappedStatement);
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