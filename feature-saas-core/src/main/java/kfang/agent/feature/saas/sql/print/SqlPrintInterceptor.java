package kfang.agent.feature.saas.sql.print;

import cn.hyugatool.core.date.DateFormat;
import cn.hyugatool.core.date.DateUtil;
import cn.hyugatool.core.date.interval.TimeInterval;
import cn.hyugatool.core.lang.Console;
import cn.hyugatool.core.string.StringUtil;
import kfang.infra.service.datasource.mybatis.interceptor.PaginationInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

/**
 * Sql打印拦截器
 * 拼接SQL参数
 *
 * @author hyuga
 * @since 2018-04-26 15:09
 */
@Intercepts({
        @Signature(type = Executor.class
                , method = "query"
                , args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class
                , method = "update"
                , args = {MappedStatement.class, Object.class}),
        @Signature(
                type = Executor.class
                , method = "query"
                , args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
        )
})
public class SqlPrintInterceptor implements Interceptor {

    private static final Log logger = LogFactory.getLog(SqlPrintInterceptor.class);

    private final Map<String, BoundSql> sqlMappings = new ConcurrentHashMap<>();

    public void putSqlMapping(String statementId, BoundSql sql) {
        sqlMappings.put(statementId, sql);
    }

    public boolean containsSql(String statementId) {
        return sqlMappings.containsKey(statementId);
    }

    public void removeSql(String statementId) {
        sqlMappings.remove(statementId);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.start();

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameterObject = null;
        if (invocation.getArgs().length > 1) {
            parameterObject = invocation.getArgs()[1];
            if (parameterObject instanceof PaginationInterceptor.CountParameter countParameter) {
                parameterObject = countParameter.getParameter();
            }
        }

        Object result = invocation.proceed();

        String statementId = mappedStatement.getId();
        BoundSql boundSql = sqlMappings.get(statementId);
        if (boundSql == null) {
            boundSql = mappedStatement.getBoundSql(parameterObject);
        }
        Configuration configuration = mappedStatement.getConfiguration();
        String sql = getSql(boundSql, parameterObject, configuration);

        if (logger.isInfoEnabled()) {
            // 测试环境才注入 可以忽略性能影响
            synchronized (this) {
                String theStringLine = StringUtil.repeatAndJoin("=", 150, "");
                Console.magentaLog(theStringLine);
                Console.greenLog("【SQL】执行耗时: {}", timeInterval.intervalPretty());
                Console.yellowLog("【SQL】执行方法：{}", statementId);
                Console.blueLog("【SQL】执行语句：{}", sql);
                Console.magentaLog(theStringLine);
            }
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor executor) {
            return Plugin.wrap(executor, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private String getSql(BoundSql boundSql, Object parameterObject, Configuration configuration) {
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        if (parameterMappings != null) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    sql = replacePlaceholder(sql, value);
                }
            }
        }
        return sql;
    }

    private String replacePlaceholder(String sql, Object propertyValue) {
        String result;
        if (propertyValue != null) {
            if (propertyValue instanceof String str) {
                result = "'" + str + "'";
            } else if (propertyValue instanceof Date date) {
                result = "'" + DateUtil.format(DateFormat.yyyy_MM_dd_HH_mm_ss, date) + "'";
            } else {
                result = propertyValue.toString();
            }
        } else {
            result = "null";
        }
        return sql.replaceFirst("\\?", Matcher.quoteReplacement(result));
    }

}