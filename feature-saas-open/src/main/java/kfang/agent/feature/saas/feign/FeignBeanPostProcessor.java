package kfang.agent.feature.saas.feign;

import cn.hyugatool.core.instance.ReflectionUtil;
import feign.InvocationHandlerFactory;
import feign.Target;
import kfang.agent.feature.saas.constants.SaasConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FeignBeanPostProcessor
 *
 * @author pengqinglong
 * @since 2022/4/9
 */
@Component
@Profile({SaasConstants.DEV})
public class FeignBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        try {
            InjectionMetadata resourceMetadata = buildResourceMetadata(bean.getClass());
            resourceMetadata.inject(bean, beanName, null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return bean;
    }

    private InjectionMetadata buildResourceMetadata(final Class<?> clazz) {
        List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
        Class<?> targetClass = clazz;

        do {
            final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();

            ReflectionUtils.doWithLocalFields(targetClass, field -> {
               if (field.isAnnotationPresent(Resource.class)) {
                    currElements.add(new ResourceElement(field, field, null));
                }
            });

            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);

        return new InjectionMetadata(clazz, elements);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 2;
    }

    private class ResourceElement extends InjectionMetadata.InjectedElement {

        public ResourceElement(Member member, AnnotatedElement ae, @Nullable PropertyDescriptor pd) {
            super(member, pd);
        }

        @Override
        protected void inject(Object target, @Nullable String requestingBeanName, PropertyValues pvs)
                throws Throwable {

            Field field = (Field) this.member;

            ReflectionUtils.makeAccessible(field);
            FeignConfig annotation = field.getAnnotation(FeignConfig.class);
            if(annotation == null){
                return;
            }

            Object springObj = field.get(target);
            if(springObj == null){
                return;
            }
            String packageName = springObj.getClass().getInterfaces()[0].getPackageName();

            if (!(packageName.contains("agent") && packageName.contains("restful"))) {
                return;
            }

            Object springProxy = ReflectionUtil.getFieldValue(springObj, "h");
            Object advised = ReflectionUtil.getFieldValue(springProxy, "advised");
            Object targetSource = ReflectionUtil.getFieldValue(advised, "targetSource");
            Object feignTarget = ReflectionUtil.getFieldValue(targetSource, "target");
            Object feignSource = ReflectionUtil.getFieldValue(feignTarget, "h");
            Object hardCodedTarget = ReflectionUtil.getFieldValue(feignSource, "target");
            if(!(hardCodedTarget instanceof Target.HardCodedTarget)){
                return;
            }
            if(hardCodedTarget instanceof FeignProxy){
                return;
            }
            Target.HardCodedTarget agentFeignTarget = (Target.HardCodedTarget) hardCodedTarget;
            FeignProxy feignProxy = new FeignProxy<>(agentFeignTarget, annotation.value().getSuffix());
            ReflectionUtil.setFieldValue(feignSource, "target", feignProxy);

            Map<Method, InvocationHandlerFactory.MethodHandler> dispatch = (Map)ReflectionUtil.getFieldValue(feignSource, "dispatch");
            for (InvocationHandlerFactory.MethodHandler handler : dispatch.values()) {
                ReflectionUtil.setFieldValue(handler, "target", feignProxy);
            }
        }
    }

}