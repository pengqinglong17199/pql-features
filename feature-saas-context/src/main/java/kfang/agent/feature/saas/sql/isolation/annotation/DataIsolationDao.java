package kfang.agent.feature.saas.sql.isolation.annotation;

import kfang.agent.feature.saas.sql.isolation.enums.Level;

import java.lang.annotation.*;

/**
 * 数据隔离dao层顶层接口
 *
 * @author pengqinglong
 * @since 2021/12/15
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataIsolationDao {

    /**
     * 隔离级别 默认平台级别隔离
     */
    Level level() default Level.PLATFORM;
}