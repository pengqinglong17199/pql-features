package kfang.agent.feature.saas.request.logger.web;

import kfang.agent.feature.saas.enums.EnvironmentEnum;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 盘客web端请求日志参数处理
 *
 * @author pengqinglong
 * @since 2022-01-10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({AgentWebRequestLogConfiguration.class, AgentWebRequestLogCore.class})
public @interface AgentWebRequestLog {

    boolean request() default false;

    boolean cost() default false;

    EnvironmentEnum[] env() default EnvironmentEnum.DEV;

    long costThresholdMs() default 3000;

}
