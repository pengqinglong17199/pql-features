package kfang.agent.feature.saas.request.limit;

import kfang.agent.feature.saas.constants.SaasErrorCode;
import kfang.agent.feature.saas.parse.time.constant.LimitTime;

import java.lang.annotation.*;

/**
 * 请求次数限制组合注解
 *
 * @author pengqinglong
 * @since 2022/2/15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AgentRequestLimits {

    /**
     * 支持多个不同的格式的限制
     */
    AgentRequestLimit[] value();
}
