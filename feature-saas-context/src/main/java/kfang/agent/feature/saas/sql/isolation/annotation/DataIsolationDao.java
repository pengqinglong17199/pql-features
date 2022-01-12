package kfang.agent.feature.saas.sql.isolation.annotation;

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
}