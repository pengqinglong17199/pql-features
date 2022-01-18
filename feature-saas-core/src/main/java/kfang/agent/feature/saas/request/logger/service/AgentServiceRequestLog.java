package kfang.agent.feature.saas.request.logger.service;

import kfang.agent.feature.saas.request.logger.service.filter.AgentServiceRequestSleuthLogFilter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 监控开启注解
 *
 * @author 彭清龙
 * @date 2021-12-01 10:38:12
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({AgentServiceRequestSleuthLogFilter.class})
@Documented
@Inherited
public @interface AgentServiceRequestLog {

}
