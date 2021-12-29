package kfang.agent.feature.saas.sql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * 盘客sql核心累
 *
 * @author pengqinglong
 * @since 2021/12/29
 */
@Component
@Slf4j
public class AgentSqlConfiguration implements ImportBeanDefinitionRegistrar {

    private static boolean PRINT;

    private static boolean ISOLATION;

    public static boolean isPrint() {
        return PRINT;
    }

    public static boolean isIsolation() {
        return ISOLATION;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, @Nonnull BeanDefinitionRegistry registry) {
        Map<String, Object> defaultAttrs = metadata.getAnnotationAttributes(AgentSql.class.getName());
        if (defaultAttrs == null) {
            log.info("AgentSql init fail~");
            return;
        }
        PRINT = (boolean) defaultAttrs.get("print");

        ISOLATION = (boolean) defaultAttrs.get("print");

        log.info("AgentSql init success~");
    }

}