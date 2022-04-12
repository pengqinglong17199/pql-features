package kfang.agent.feature.saas.feign;

import java.lang.annotation.*;

/**
 * fegin字段配置
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignConfig {

    AgentEnvironmentEnum value();
}
