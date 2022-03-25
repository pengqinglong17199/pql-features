package kfang.agent.feature.saas.request.basicparam;

import kfang.infra.api.validate.extend.OperateExtendForm;
import kfang.infra.api.validate.extend.PageExtendForm;
import kfang.infra.common.model.LoginDto;
import kfang.infra.web.controller.BaseController;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * SecurityKeyInjectAspect
 *
 * @author hyuga
 * @since 2021-03-11
 */
@RefreshScope
@Aspect
@Component
public class RequestBasicParamsInjectAspect {

    private BaseController baseController;

    /**
     * 匹配所有的
     */
    @Pointcut("(@annotation(org.springframework.web.bind.annotation.GetMapping)" +
            "||@annotation(org.springframework.web.bind.annotation.PostMapping)" +
            "||@annotation(org.springframework.web.bind.annotation.RequestMapping))" +
            "&& execution(* *..controller.security..*.*(..))")
    private void pointCutMethodService() {
    }

    @Before("pointCutMethodService()")
    public void paramHandler(JoinPoint joinPoint) {
        Object[] argc = joinPoint.getArgs();
        for (Object object : argc) {
            if (null == object) {
                continue;
            }
            if (object instanceof OperateExtendForm || object instanceof PageExtendForm) {
                baseController = getBaseControllerBean();
                HttpServletRequest request = baseController.getRequest();
                String operatorIp = baseController.getIpAddr(request)[0];
                LoginDto currentPerson = baseController.getCurrentPerson();
                RequestBasicParamsProcessor.setBasicParams(object, request, currentPerson, operatorIp);
            }
        }
    }

    private BaseController getBaseControllerBean() {
        if (baseController == null) {
            baseController = new BaseController();
        }
        return baseController;
    }

}
