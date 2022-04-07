package kfang.agent.feature.saas.request.basicparam;

import cn.hyugatool.aop.aspectj.AspectInject;
import kfang.infra.api.validate.extend.OperateExtendForm;
import kfang.infra.api.validate.extend.PageExtendForm;
import kfang.infra.common.model.LoginDto;
import kfang.infra.web.controller.BaseController;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
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
public class RequestBasicParamsInjectAspect implements AspectInject {

    private BaseController baseController;

    @Override
    @Pointcut("(@annotation(org.springframework.web.bind.annotation.GetMapping)" +
            "||@annotation(org.springframework.web.bind.annotation.PostMapping)" +
            "||@annotation(org.springframework.web.bind.annotation.RequestMapping))" +
            "&& (execution(* *..controller.security..*.*(..)) || execution(* *..controller.safety..*.*(..)))")
    public void pointcut() {

    }

    @Override
    public void before(JoinPoint joinPoint) {
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

    @Override
    public void afterReturning(JoinPoint joinPoint, Object result) {

    }

    @Override
    public void after(JoinPoint joinPoint) {

    }

    @Override
    public void afterThrowing(Throwable exception) {

    }

    private BaseController getBaseControllerBean() {
        if (baseController == null) {
            baseController = new BaseController();
        }
        return baseController;
    }

}
