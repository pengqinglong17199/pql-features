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

    /**
     * d打印请求信息
     */
    boolean request() default false;

    /**
     * 打印请求耗时信息
     */
    boolean cost() default false;

    /**
     * 生效环境
     */
    EnvironmentEnum[] env() default EnvironmentEnum.DEV;

    /**
     * 请求耗时阈值毫秒
     */
    long costThresholdMs() default 3000;

}
