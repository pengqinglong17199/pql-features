package kfang.agent.feature.saas.request.limit;

import kfang.agent.feature.saas.constants.SaasErrorCode;
import kfang.agent.feature.saas.parse.time.constant.LimitTime;

import java.lang.annotation.*;

/**
 * 请求次数限制注解
 *
 * @author pengqinglong
 * @since 2022/2/15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AgentRequestLimit {

    String name();

    long limit();

    String[] removes() default {};

    /**
     * 使用{@link LimitTime}定义好的时间进行控制
     * 默认为{@link LimitTime#TO_DAY}限制到当天24：00
     *
     * 支持自定义格式
     * 1. ?y?M?d?h?m?s
     * 2. ?年?月?天?时?分?秒
     */
    String time() default LimitTime.TO_DAY;

    /**
     * 到达限制后的自定义返回错误码
     */
    String code() default SaasErrorCode.E9001;

    /**
     * 到达限制后的自定义返回错误码
     */
    String message() default "当前请求次数已达上限";
}
