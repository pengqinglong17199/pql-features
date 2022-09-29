package kfang.agent.feature.saas.request.service.validation.aop;

import cn.hyugatool.aop.aspectj.AspectAroundInject;
import com.alibaba.fastjson2.JSON;
import kfang.infra.api.JsonCommonCodeEnum;
import kfang.infra.api.RequestResult;
import kfang.infra.api.ValidationForm;
import kfang.infra.common.ContextHolder;
import kfang.infra.web.common.validation.HasSpringValidationContextKey;
import kfang.infra.web.common.validation.ValidationResult;
import kfang.infra.web.common.validation.pool.ValidationPoolBean;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * ServiceParamsValidationAspect
 * <p>
 * 服务层参数校验切面
 *
 * @author hyuga
 * @since 2022-03-13 15:52:05
 */
@Aspect
@Component
public class ServiceParamsValidationAspect implements AspectAroundInject {

    @Resource
    protected ValidationPoolBean validationPoolBean;

    private final String mircoServiceReturnType = RequestResult.class.getSimpleName();

    @Override
    @Pointcut("execution(* com.kfang.service..*.api..*.*(..))")
    public void pointcut() {

    }

    @Override
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        return doValidate(pjp);
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
            if (arg instanceof ValidationForm validationForm) {
                ValidationResult result = validationPoolBean.validationParam(validationForm);
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
