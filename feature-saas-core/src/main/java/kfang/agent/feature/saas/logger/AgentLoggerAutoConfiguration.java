package kfang.agent.feature.saas.logger;

import kfang.agent.feature.saas.logger.core.AgentLoggerAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AgentLoggerConfig
 *
 * @author pengqinglong
 * @since 2022/1/14
 */
@Configuration
@Slf4j
public class AgentLoggerAutoConfiguration {

    @Bean
    public AgentLoggerAspect agentLoggerAspect() {
        log.info("AgentLoggerAutoConfiguration init success~");
        return new AgentLoggerAspect();
    }
}