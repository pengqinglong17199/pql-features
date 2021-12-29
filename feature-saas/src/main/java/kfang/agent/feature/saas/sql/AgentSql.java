package kfang.agent.feature.saas.sql;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 盘客sql处理注解
 * @author pengqinglong
 * @since 2021-12-29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({AgentSqlConfiguration.class, AgentSqlCore.class})
public @interface AgentSql {

    /**
     * 是否打印sql 默认开启 测试环境有效 正式环境无效 不会注入
     */
    boolean print() default true;

    /**
     * 是否开启数据隔离
     */
    boolean isolation();
}
