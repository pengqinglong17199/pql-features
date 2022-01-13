package kfang.agent.feature.saas.request.logger;

import kfang.agent.feature.saas.enums.EnvironmentEnum;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 盘客请求日志参数处理
 *
 * @author pengqinglong
 * @since 2022-01-10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({AgentRequestLogConfiguration.class, AgentRequestLogCore.class})
public @interface AgentRequestLog {

    boolean request() default false;

    boolean cost() default false;

    EnvironmentEnum[] env() default EnvironmentEnum.DEV;

    long costThresholdMs() default 3000;

}
