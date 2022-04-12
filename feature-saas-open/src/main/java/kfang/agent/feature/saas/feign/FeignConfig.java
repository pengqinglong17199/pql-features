package kfang.agent.feature.saas.feign;

import java.lang.annotation.*;

/**
 * feign字段配置
 *
 * @author pengqinglong
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignConfig {

    AgentEnvironmentEnum value();

}
