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

    /**
     * 记录请求信息
     */
    boolean logParams() default true;

    /**
     * 记录登陆人(操作人)信息
     */
    boolean loginDto() default false;

    /**
     * 记录打印结果
     */
    boolean logResult() default true;

}
