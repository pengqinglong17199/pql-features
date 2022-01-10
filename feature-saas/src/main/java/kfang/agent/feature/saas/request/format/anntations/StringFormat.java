package kfang.agent.feature.saas.request.format.anntations;

import java.lang.annotation.*;

/**
 * 请求参数移除空格注解
 *
 * @author pengqinglong
 * @since 2021/11/15
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StringFormat {

    /**
     * 移除字符串前面的空格 默认启用 false为不启用
     */
    boolean front() default true;

    /**
     * 移除字符串后面的空格 默认启用
     */
    boolean rear() default true;

    /**
     * 移除字符串中的表情符 默认不启用
     */
    boolean emoji() default false;

}