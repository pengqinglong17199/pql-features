package kfang.agent.feature.monitor;

import kfang.agent.feature.monitor.annotation.EnableMonitor;
import kfang.agent.feature.monitor.enums.ProjectEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * MonitorConfiguration
 *
 * @author hyuga
 * @since 2022/01/12
 */
@Component
@Slf4j
public class MonitorConfiguration implements ImportBeanDefinitionRegistrar {

    private static ProjectEnum PROJECT;

    public static ProjectEnum project() {
        return PROJECT;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, @Nonnull BeanDefinitionRegistry registry) {
        Map<String, Object> defaultAttrs = metadata.getAnnotationAttributes(EnableMonitor.class.getName());
        if (defaultAttrs == null) {
            log.info("Monitor init fail~");
            return;
        }

        PROJECT = (ProjectEnum) defaultAttrs.get("project");

        log.info("Monitor init success~");
    }

}