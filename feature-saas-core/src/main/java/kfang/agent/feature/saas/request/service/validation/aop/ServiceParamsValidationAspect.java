package kfang.agent.feature.saas.request.service.validation.aop;

import com.alibaba.fastjson.JSON;
import kfang.infra.api.JsonCommonCodeEnum;
import kfang.infra.api.RequestResult;
import kfang.infra.api.ValidationForm;
import kfang.infra.common.ContextHolder;
import kfang.infra.web.common.validation.HasSpringValidationContextKey;
import kfang.infra.web.common.validation.ValidationResult;
import kfang.infra.web.common.validation.pool.ValidationPoolBean;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hyuga
 * @since 2022-03-13 15:52:05
 */
@Aspect
@Component
public class ServiceParamsValidationAspect {

    @Resource
    protected ValidationPoolBean validationPoolBean;

    private final String mircoServiceReturnType = RequestResult.class.getSimpleName();

    @Pointcut("execution(* com.kfang.service..*.api..*.*(..))")
    private void cutMethod() {
    }

    @Around("cutMethod()")
    public Object validate(ProceedingJoinPoint joinPoint) throws Throwable {
        return doValidate(joinPoint);
    }

    private Object doValidate(ProceedingJoinPoint joinPoint) throws Throwable {
        Boolean hasSpringValidation = (Boolean) ContextHolder.getContextData(HasSpringValidationContextKey.class);
        if (hasSpringValidation == null || hasSpringValidation) {
            return joinPoint.proceed();
        }
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return joinPoint.proceed();
        }
        for (Object arg : args) {
            if (arg instanceof ValidationForm) {
                ValidationResult result = validationPoolBean.validationParam((ValidationForm) arg);
                if (result.hasError()) {
                    if (joinPoint.getSignature().toLongString().contains(mircoServiceReturnType)) {
                        return new RequestResult<>(false, null, result.getErrorMessages().get(0).getMessage());
                    } else {
                        Map<String, Object> jsonResult = new HashMap<>();
                        jsonResult.put("status", JsonCommonCodeEnum.E0002.getStatus());
                        jsonResult.put("message", result.getErrorMessages().get(0).getMessage());
                        jsonResult.put("result", null);
                        return JSON.toJSONString(jsonResult);
                    }
                }
            }
        }
        return joinPoint.proceed();
    }

}