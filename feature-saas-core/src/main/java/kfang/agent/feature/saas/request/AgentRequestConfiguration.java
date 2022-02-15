package kfang.agent.feature.saas.request;

import kfang.agent.feature.saas.request.basicparam.RequestBasicParamsInjectAspect;
import kfang.agent.feature.saas.request.limit.AgentRequestLimitCore;
import kfang.agent.feature.saas.sql.AgentSql;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * 盘客请求模块核心配置类
 *
 * @author pengqinglong
 * @since 2022/2/15
 */
@Slf4j
public class AgentRequestConfiguration implements ImportBeanDefinitionRegistrar {

    private static boolean basicParam;
    private static boolean requestLimit;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, @Nonnull BeanDefinitionRegistry registry) {
        Map<String, Object> defaultAttrs = metadata.getAnnotationAttributes(AgentRequest.class.getName());
        if (defaultAttrs == null) {
            log.info("AgentRequest init fail~");
            return;
        }

        basicParam = (boolean) defaultAttrs.get("basicParam");
        if(basicParam) {
            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(RequestBasicParamsInjectAspect.class).getBeanDefinition();
            registry.registerBeanDefinition("requestBasicParamsInjectAspect", beanDefinition);
        }

        requestLimit = (boolean) defaultAttrs.get("requestLimit");
        if(requestLimit) {
            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(AgentRequestLimitCore.class).getBeanDefinition();
            registry.registerBeanDefinition("agentRequestLimitCore", beanDefinition);
        }

        log.info("AgentRequest init success~");
    }

    public static boolean isBasicParam() {
        return basicParam;
    }

    public static boolean isRequestLimit() {
        return requestLimit;
    }
}