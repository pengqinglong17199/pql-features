package kfang.agent.feature.saas.request.commit.annotations;

import java.lang.annotation.*;

/**
 * 重复请求注解 防止重复请求
 *
 * @author pengqinglong
 * @since 2021/10/11
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatedRequests {

    /**
     * 重复请求间隔时间 单位 毫秒
     */
    long time() default 1000;
}