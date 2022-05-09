package kfang.agent.feature.saas.feign;

import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.system.NetworkUtil;
import cn.hyugatool.system.SystemUtil;
import kfang.agent.feature.saas.constants.FeignConstants;
import kfang.agent.feature.saas.constants.SaasConstants;
import kfang.agent.feature.saas.feign.enums.ServiceSignEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
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
    private static boolean SKIP_SPECIAL_IP_ADDRESS_SEGMENT;
    private static boolean WEB_CALL_EXCEPTION_CAPTURE;
    private static String[] WEB_ENV;

    public static boolean isIsolation() {
        return ISOLATION;
    }

    public static ServiceSignEnum serviceSign() {
        return SERVICE_SIGN;
    }

    public static boolean skipSpecialIpAddressSegment() {
        return SKIP_SPECIAL_IP_ADDRESS_SEGMENT;
    }

    public static boolean webCallExceptionCapture() {
        return WEB_CALL_EXCEPTION_CAPTURE;
    }

    public static String[] webEnv() {
        return WEB_ENV;
    }

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata metadata, @Nonnull BeanDefinitionRegistry registry) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) registry;
        ConfigurableEnvironment environment = (ConfigurableEnvironment) beanFactory.getBean(ConfigurableApplicationContext.ENVIRONMENT_BEAN_NAME);
        String property = environment.getProperty("kfang.infra.common.env.deploy");
        boolean isDevEnv = StringUtil.equalsIgnoreCase(property, SaasConstants.DEV);

        Map<String, Object> defaultAttrs = metadata.getAnnotationAttributes(AgentFeign.class.getName());
        if (defaultAttrs == null) {
            log.info("AgentFeign init fail~");
            return;
        }

        ISOLATION = (boolean) defaultAttrs.get("isolation");
        SERVICE_SIGN = ServiceSignEnum.valueOf(String.valueOf(defaultAttrs.get("serviceSign")));
        SKIP_SPECIAL_IP_ADDRESS_SEGMENT = (boolean) defaultAttrs.get("skipSpecialIpAddressSegment");
        WEB_CALL_EXCEPTION_CAPTURE = (boolean) defaultAttrs.get("webCallExceptionCapture");
        WEB_ENV = (String[]) defaultAttrs.get("webEnv");

        if (!isDevEnv) {
            // agentFeign隔离部分仅对测试环境生效
            return;
        }

        if (ISOLATION) {
            feignIsolation();
        } else {
            System.setProperty(FeignConstants.FEIGN_SUFFIX, StringUtil.EMPTY);
        }

        log.info("AgentFeign init success~");
    }

    private void feignIsolation() {
        String localIpAddr = NetworkUtil.getLocalIpAddr();

        // 开启特殊ip段校验，且IP属于特殊IP段
        boolean isSpecialIp = SKIP_SPECIAL_IP_ADDRESS_SEGMENT && isSpecialIpAddressSegment(localIpAddr);
        if (isSpecialIp) {
            log.info("当前服务IP属于特殊IP网段192.168.3.*，允许启动，且强制服务隔离");
        } else {
            // 校验本地IP地址是否允许的开发ip或测试服务器ip
            FeignConstants.ipWhetherNeedIsolation(localIpAddr);
        }

        if (FeignConstants.isTestEnvironment(localIpAddr)) {
            // 测试环境非默认54，统一加后缀，后缀为ip末位
            String suffixOfTest = FeignConstants.getSuffixOfTest(localIpAddr);
            if (StringUtil.isEmpty(suffixOfTest)) {
                System.setProperty(FeignConstants.FEIGN_SUFFIX, StringUtil.EMPTY);
            } else {
                System.setProperty(FeignConstants.FEIGN_SUFFIX, "-" + suffixOfTest);
            }
        } else if (FeignConstants.isDeveloperLocalEnvironment(localIpAddr) || isSpecialIp) {
            // 开发本地服务启动，统一加后缀，后缀为本地电脑名
            System.setProperty(FeignConstants.FEIGN_SUFFIX, "-" + SystemUtil.getLocalHostName());
        } else {
            System.setProperty(FeignConstants.FEIGN_SUFFIX, StringUtil.EMPTY);
        }
    }

    public static boolean isSpecialIpAddressSegment(String localIpAddr) {
        String substringOfLocalIpAddr = localIpAddr.substring(0, localIpAddr.lastIndexOf(".") + 1);
        return StringUtil.equals("192.168.3.", substringOfLocalIpAddr);
    }

}