package kfang.agent.feature.saas.feign;

import cn.hyugatool.core.collection.ArrayUtil;
import kfang.agent.feature.saas.constants.SaasConstants;
import kfang.agent.feature.saas.feign.core.FeignBuilderHelper;
import kfang.agent.feature.saas.feign.exception.FeignExceptionHandler;
import kfang.infra.common.KfangInfraCommonProperties;
import kfang.infra.common.spring.SpringBeanPicker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 盘客feign核心类
 *
 * @author pengqinglong
 * @since 2021/12/29
 */
@Component
@Slf4j
public class AgentFeignCore implements ApplicationRunner {

    @Bean
    @ConditionalOnProperty(value = KfangInfraCommonProperties.ENV_DEPLOY, havingValue = SaasConstants.DEV)
    public FeignBuilderHelper feignBuilderHelper() {
        return new FeignBuilderHelper();
    }

    @Bean
    public FeignExceptionHandler feignExceptionHandler() {
        boolean webCallExceptionCapture = AgentFeignConfiguration.webCallExceptionCapture();
        String[] webEnv = AgentFeignConfiguration.webEnv();
        String currentEnv = SpringBeanPicker.getBean(KfangInfraCommonProperties.class).getEnv().getDeploy();
        if (webCallExceptionCapture && ArrayUtil.contains(webEnv, currentEnv)) {
            log.info("agentFeign webCallExceptionCapture init.");
            return new FeignExceptionHandler();
        }
        return null;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("AgentFeign start success~");
    }

}