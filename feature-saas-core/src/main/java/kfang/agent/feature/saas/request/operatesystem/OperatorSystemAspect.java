package kfang.agent.feature.saas.request.operatesystem;

import cn.hyugatool.aop.AnnotationUtil;
import cn.hyugatool.aop.aspectj.AspectInject;
import cn.hyugatool.core.clazz.ClassUtil;
import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.instance.ReflectionUtil;
import kfang.infra.api.validate.extend.OperateExtendForm;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * 操作系统来源赋值切面
 *
 * @author hyuga
 * @since 2021/12/20
 */
@Slf4j
@Aspect
@Component
public class OperatorSystemAspect implements AspectInject {

    private static final String OPERATOR_SYSTEM = "operatorSystem";

    @Override
    @Pointcut("execution(* com.kfang.web..*.controller..*.*(..))")
    public void pointcut() {

    }

    @Override
    public void before(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof OperateExtendForm operateExtendForm) {
                OperatorSystem operatorSystem = AnnotationUtil.getDeclaredAnnotation(joinPoint, OperatorSystem.class);
                if (Objects.isNull(operatorSystem)) {
                    return;
                }
                boolean penetration = operatorSystem.penetration();
                setOperateSystemValue(operateExtendForm, operatorSystem, penetration);
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

    private void setOperateSystemValue(Object arg, OperatorSystem operatorSystem, boolean penetration) {
        String system = operatorSystem.system();
        ReflectionUtil.setFieldValue(arg, OPERATOR_SYSTEM, system);
        if (!penetration) {
            return;
        }
        List<Field> declaredFields = ReflectionUtil.getDeclaredFields(arg);
        ListUtil.optimize(declaredFields)
                .stream().filter(field -> !ClassUtil.isBasicType(field))
                .forEach(field -> {
                    Object fieldObject = ReflectionUtil.getFieldValue(arg, field);
                    if (fieldObject instanceof OperateExtendForm operateExtendForm) {
                        // 仅渗透一层，避免死循环嵌套
                        setOperateSystemValue(operateExtendForm, operatorSystem, false);
                    }
                });
    }

}