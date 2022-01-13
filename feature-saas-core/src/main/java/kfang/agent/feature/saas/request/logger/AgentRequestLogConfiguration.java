package kfang.agent.feature.saas.request.logger;

import kfang.agent.feature.saas.enums.EnvironmentEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * 盘客feign核心类
 *
 * @author hyuga
 * @since 2022/01/07
 */
@Component
@Slf4j
public class AgentRequestLogConfiguration implements ImportBeanDefinitionRegistrar {

    private static boolean REQUEST;
    private static boolean COST;
    private static EnvironmentEnum[] ENV;
    private static long COST_THRESHOLD_MS;

    public static boolean isRequest() {
        return REQUEST;
    }

    public static boolean isCost() {
        return COST;
    }

    public static EnvironmentEnum[] env() {
        return ENV;
    }

    public static long costThresholdMs() {
        return COST_THRESHOLD_MS;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, @Nonnull BeanDefinitionRegistry registry) {
        Map<String, Object> defaultAttrs = metadata.getAnnotationAttributes(AgentRequestLog.class.getName());
        if (defaultAttrs == null) {
            log.info("AgentRequestLog init fail~");
            return;
        }

        REQUEST = (boolean) defaultAttrs.get("request");
        COST = (boolean) defaultAttrs.get("cost");
        ENV = (EnvironmentEnum[]) defaultAttrs.get("env");
        COST_THRESHOLD_MS = Long.parseLong(String.valueOf(defaultAttrs.get("costThresholdMs")));

        log.info("AgentRequestLog init success~");
    }

}