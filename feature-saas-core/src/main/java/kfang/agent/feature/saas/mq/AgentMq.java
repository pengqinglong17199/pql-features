package kfang.agent.feature.saas.mq;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * AgentMq
 *
 * @author pengqinglong
 * @since 2022/4/1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({AgentRabbitMqConfiguration.class})
public @interface AgentMq {
}