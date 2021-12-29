package kfang.agent.feature.saas.sql;

import kfang.agent.feature.saas.constants.SaasConstants;
import kfang.agent.feature.saas.sql.isolation.core.DataIsolationInterceptor;
import kfang.agent.feature.saas.sql.print.SqlPrintInterceptor;
import kfang.infra.common.KfangInfraCommonProperties;
import kfang.infra.common.spring.SpringBeanPicker;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
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
public class AgentSqlCore implements ApplicationRunner {


    @Resource
    private KfangInfraCommonProperties kfangInfraCommonProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        SqlSessionFactory bean = SpringBeanPicker.getBean(SqlSessionFactory.class);
        Configuration configuration = bean.getConfiguration();

        // 打印拦截器特殊环境处理
        String deploy = kfangInfraCommonProperties.getEnv().getDeploy();
        if(Objects.equals(SaasConstants.DEV, deploy)){
            this.addInterceptor(configuration, AgentSqlConfiguration.isPrint(), new SqlPrintInterceptor());
        }

        // 注入拦截器
        this.addInterceptor(configuration, AgentSqlConfiguration.isIsolation(), new DataIsolationInterceptor());

        log.info("AgentSql start success~");
    }

    private void addInterceptor(Configuration configuration, boolean flag, Interceptor interceptor) {
        if(flag){
            configuration.addInterceptor(interceptor);
        }
    }
}