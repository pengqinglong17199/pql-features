package kfang.agent.feature.saas.request.logger.service;

import kfang.agent.feature.saas.request.logger.service.filter.AgentServiceRequestSleuthLogFilter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 服务端请求日志启动注解
 * <p>
 * 提取web请求操作人相关信息赋值到MDC，用于logback.xml打印操作人信息
 *
 * @author hyuga
 * @date 2021-12-01 10:38:12
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({AgentServiceRequestSleuthLogFilter.class})
@Documented
@Inherited
public @interface AgentServiceRequestLog {

}
