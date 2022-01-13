package kfang.agent.feature.saas.processor;

import cn.hyugatool.core.jar.JarUtil;
import cn.hyugatool.core.string.StringUtil;
import kfang.agent.feature.saas.constants.FeignConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 环境后置处理程序
 * 本地启动test单测未读取application.yml导致启动异常问题解决
 *
 * @author chenyi
 * @since 2021/6/9
 */
public class AgentEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (JarUtil.isJarRun(AgentEnvironmentPostProcessor.class)) {
            return;
        }
        // 本地单元测试赋值spring.application.name后缀
        System.setProperty(FeignConstants.FEIGN_SUFFIX, StringUtil.EMPTY);
    }

}

