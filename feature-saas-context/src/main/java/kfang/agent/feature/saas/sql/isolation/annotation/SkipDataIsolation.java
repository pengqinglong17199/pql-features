package kfang.agent.feature.saas.sql.isolation.annotation;

import kfang.agent.feature.saas.sql.isolation.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.*;

/**
 * 跳过数据隔离注解
 *
 * @author pengqinglong
 * @since 2021/12/15
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SkipDataIsolation {

    /**
     * 需要跳过的数据级别
     */
    Level level() default Level.ALL;

    /**
     * 跳过的原因备注
     */
    String note() default "";

}
