package kfang.agent.feature.saas.thirdparty.annotations;

import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;

import java.lang.annotation.*;
import java.lang.reflect.Field;

/**
 * 第三方请求实体注解
 * 该注解直接打在{@link ThirdpartyForm}的{@link Field}实现上
 *
 * @author pengqinglong
 * @since 2022/3/14
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthParam {

    /**
     * 字段名 重命名
     */
    String value() default "";
}
