package kfang.agent.feature.saas.sql;

import kfang.agent.feature.saas.constants.SaasConstants;
import kfang.agent.feature.saas.sql.isolation.core.DataIsolationInterceptor;
import kfang.agent.feature.saas.sql.print.SqlPrintInterceptor;
import kfang.infra.common.KfangInfraCommonProperties;
import kfang.infra.common.spring.SpringBeanPicker;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * 盘客sql核心累
 *
 * @author pengqinglong
 * @since 2021/12/29
 */
@Component
@Slf4j
public class AgentSqlConfiguration implements ImportBeanDefinitionRegistrar {

    @Getter
    private static boolean PRINT;

    @Getter
    private static boolean ISOLATION;

    public static boolean isPrint() {
        return PRINT;
    }

    public static boolean isIsolation() {
        return ISOLATION;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Map<String, Object> defaultAttrs = metadata.getAnnotationAttributes(AgentSql.class.getName());
        if(defaultAttrs == null){
            log.info("AgentSql init fail~");
            return;
        }
        PRINT = (boolean)defaultAttrs.get("print");

        ISOLATION = (boolean)defaultAttrs.get("print");

        log.info("AgentSql init success~");
    }
}