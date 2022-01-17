package kfang.agent.feature.saas.request.operatesystem;

import cn.hyugatool.core.clazz.ClassUtil;
import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.instance.ReflectionUtil;
import cn.hyugatool.extra.aop.AnnotationUtil;
import kfang.infra.api.validate.extend.OperateExtendForm;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;


/**
 * 请求参数格式化切面
 *
 * @author hyuga
 * @since 2021/12/20
 */
@Slf4j
@Aspect
@Component
public class OperatorSystemAspect {

    private static final String OPERATOR_SYSTEM = "operatorSystem";

    @Pointcut("execution(* com.kfang.web..*.controller..*.*(..))")
    private void cutMethod() {
    }

    /**
     * 前置通知：在目标方法执行前调用
     */
    @Before("cutMethod()")
    public void begin(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof OperateExtendForm) {
                OperatorSystem operatorSystem = AnnotationUtil.getDeclaredAnnotation(joinPoint, OperatorSystem.class);
                if (Objects.isNull(operatorSystem)) {
                    return;
                }
                boolean penetration = operatorSystem.penetration();
                setOperateSystemValue(arg, operatorSystem, penetration);
            }
        }
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
                    if (fieldObject instanceof OperateExtendForm) {
                        // 仅渗透一层，避免死循环嵌套
                        setOperateSystemValue(fieldObject, operatorSystem, false);
                    }
                });
    }

}