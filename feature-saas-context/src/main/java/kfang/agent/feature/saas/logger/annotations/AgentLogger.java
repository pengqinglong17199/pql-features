package kfang.agent.feature.saas.logger.annotations;

import kfang.agent.feature.saas.logger.LogModule;

import java.lang.annotation.*;

/**
 * 盘客切面日志注解
 *
 * @author hyuga
 * @since 2021-04-17
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AgentLogger {

    /**
     * 日志模块
     */
    String logModule();

    Class<?> clazz() default AgentLogger.class;

    boolean logParams() default true;

    boolean logResult() default true;

}
