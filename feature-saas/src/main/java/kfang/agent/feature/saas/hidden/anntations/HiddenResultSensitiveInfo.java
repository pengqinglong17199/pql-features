package kfang.agent.feature.saas.hidden.anntations;


import java.lang.annotation.*;

/**
 * 隐藏返回结果敏感信息
 * 与 {@link HiddenFieldSensitiveInfo }配合才会生效
 *
 * @author pengqinglong
 * @since 2021/4/12
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HiddenResultSensitiveInfo {

}
