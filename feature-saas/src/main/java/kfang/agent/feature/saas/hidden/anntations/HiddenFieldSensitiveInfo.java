package kfang.agent.feature.saas.hidden.anntations;


import java.lang.annotation.*;

/**
 * 隐藏字段敏感信息
 * 与 {@link HiddenResultSensitiveInfo }配合才会生效
 *
 * @author pengqinglong
 * @since 2021/4/12
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HiddenFieldSensitiveInfo {

    /**
     * 替换原数据的字符串 默认为空
     * 优先级最高 如果有这个 下面全部作废
     * {@link #head}
     * {@link #tail}
     * {@link #mask}
     */
    String replaceStr() default "";

    /**
     * 数据头部保留多少位
     */
    int head() default 3;

    /**
     * 数据尾部保留多少位
     */
    int tail() default 4;

    /**
     * 数据中部隐藏掩码
     */
    char mask() default '*';

    /**
     * 数据掩码数量 此字段大于0时 {@link #tail}失效
     */
    int maskSize() default -1;
}