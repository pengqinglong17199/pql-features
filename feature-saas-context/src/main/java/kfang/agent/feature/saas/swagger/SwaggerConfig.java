package kfang.agent.feature.saas.swagger;

import kfang.agent.feature.saas.constants.SaasConstants;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * SwaggerConfig
 *
 * @author pengqinglong
 * @since 2022/3/7
 */
@Configuration
@ComponentScan(value = {"kfang.agent.feature.saas.swagger", "springfox.documentation"})
@Profile({SaasConstants.DEV})
public class SwaggerConfig {
}