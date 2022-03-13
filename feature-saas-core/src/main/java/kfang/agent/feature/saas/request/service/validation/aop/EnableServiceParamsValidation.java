package kfang.agent.feature.saas.request.service.validation.aop;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用service服务层参数校验切面
 *
 * @author hyuga
 * @since 2022/1/21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ServiceParamsValidationAspect.class)
public @interface EnableServiceParamsValidation {
}