package kfang.agent.feature.saas.feign;

import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.system.NetworkUtil;
import cn.hyugatool.system.SystemUtil;
import kfang.agent.feature.saas.constants.FeignConstants;
import kfang.agent.feature.saas.feign.enums.ServiceSignEnum;
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
public class AgentFeignConfiguration implements ImportBeanDefinitionRegistrar {

    private static boolean ISOLATION;
    private static ServiceSignEnum SERVICE_SIGN;

    public static boolean isIsolation() {
        return ISOLATION;
    }

    public static ServiceSignEnum serviceSign() {
        return SERVICE_SIGN;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, @Nonnull BeanDefinitionRegistry registry) {
        Map<String, Object> defaultAttrs = metadata.getAnnotationAttributes(AgentFeign.class.getName());
        if (defaultAttrs == null) {
            log.info("AgentFeign init fail~");
            return;
        }

        ISOLATION = (boolean) defaultAttrs.get("isolation");
        SERVICE_SIGN = ServiceSignEnum.valueOf(String.valueOf(defaultAttrs.get("serviceSign")));

        String localIpAddr = NetworkUtil.getLocalIpAddr();
        boolean isIntranetIp = localIpAddr.startsWith("10.210.");

        if (ISOLATION && isIntranetIp) {
            feignIsolation(localIpAddr);
        } else {
            System.setProperty(FeignConstants.FEIGN_SUFFIX, StringUtil.EMPTY);
        }

        log.info("AgentFeign init success~");
    }

    private void feignIsolation(String localIpAddr) {
        // 校验本地IP地址是否允许的开发ip或测试服务器ip
        FeignConstants.ipWhetherNeedIsolation(localIpAddr);

        if (FeignConstants.isTestEnvironment(localIpAddr)) {
            // 测试环境非默认54，统一加后缀，后缀为ip末位
            String suffixOfTest = FeignConstants.getSuffixOfTest(localIpAddr);
            if (StringUtil.isEmpty(suffixOfTest)) {
                System.setProperty(FeignConstants.FEIGN_SUFFIX, StringUtil.EMPTY);
            } else {
                System.setProperty(FeignConstants.FEIGN_SUFFIX, "-" + suffixOfTest);
            }
        } else if (FeignConstants.isDeveloperLocalEnvironment(localIpAddr)) {
            // 开发本地服务启动，统一加后缀，后缀为本地电脑名
            System.setProperty(FeignConstants.FEIGN_SUFFIX, "-" + SystemUtil.getLocalHostName());
        } else {
            System.setProperty(FeignConstants.FEIGN_SUFFIX, StringUtil.EMPTY);
        }
    }

}