package kfang.agent.feature.monitor.annotation;

import kfang.agent.feature.monitor.MonitorConfiguration;
import kfang.agent.feature.monitor.core.AutomaticHeartbeatMechanism;
import kfang.agent.feature.monitor.enums.ProjectEnum;
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
@Import({MonitorConfiguration.class, AutomaticHeartbeatMechanism.class})
@Documented
@Inherited
public @interface EnableMonitor {

    ProjectEnum project() default ProjectEnum.AGENT;

}
