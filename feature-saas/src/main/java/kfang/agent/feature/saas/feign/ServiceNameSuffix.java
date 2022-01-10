package kfang.agent.feature.saas.feign;

import cn.hyugatool.core.properties.PropertiesUtil;
import cn.hyugatool.core.string.StringUtil;
import kfang.agent.feature.saas.constants.FeignConstants;
import kfang.agent.feature.saas.feign.enums.DefaultEnvEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * ServeNameSuffix
 * 动态追加服务名称后缀
 * 用于服务启动自动更改application.yml的spring.application.name
 *
 * @author hyuga
 * @since 2020-12-23 下午7:16
 */
@Slf4j
public class ServiceNameSuffix {

    /**
     * 判断要调用的feign服务，是否在配置文件赋值为1
     * 1:表示调用本地服务，feignUrl追加后缀名
     * 0:表示调用测试服务，feignUrl不做处理
     *
     * @param feignServiceName feign服务名
     * @return true/false
     */
    public static int isEnabledOfLocalService(String feignServiceName) {
        return isEnabledOfLocalServiceNameDeploy(feignServiceName);
    }

    /**
     * 是否启用了本地服务名配置
     *
     * @param feignServiceName 服务名
     * @return 是否
     */
    private static int isEnabledOfLocalServiceNameDeploy(String feignServiceName) {
        try {
            Properties filePropertiesInSourceModule = PropertiesUtil.getFilePropertiesInSourceModule(FeignConstants.DYNAMIC_SERVICE_NAME_PROPERTIES);
            String dynamicServiceNameDeploy = filePropertiesInSourceModule.getProperty(feignServiceName, "0");
            return Integer.parseInt(dynamicServiceNameDeploy);
        } catch (IOException ignore) {
            // 非本地环境没有该文件
            throw new RuntimeException(String.format("开发本地环境没有该文件，请检查：%s", FeignConstants.DYNAMIC_SERVICE_NAME_PROPERTIES));
        }
    }

    /**
     * 获取默认环境IP后缀
     */
    public static String getDefaultEnvIpSuffix() {
        try {
            Properties filePropertiesInSourceModule = PropertiesUtil.getFilePropertiesInSourceModule(FeignConstants.DYNAMIC_SERVICE_NAME_PROPERTIES);
            String defaultEnvIpSuffix = filePropertiesInSourceModule.getProperty(FeignConstants.DEFAULT_ENV_IP_SUFFIX, StringUtil.EMPTY);
            if (StringUtil.isEmpty(defaultEnvIpSuffix)) {
                return StringUtil.EMPTY;
            }
            boolean noContains = !Arrays.stream(DefaultEnvEnum.values()).map(DefaultEnvEnum::getIpSuffix).collect(Collectors.toList()).contains(defaultEnvIpSuffix);
            if (noContains) {
                throw new RuntimeException(String.format("请确认测试环境是否有该服务器，IP后缀：%s", defaultEnvIpSuffix));
            }
            return "-" + defaultEnvIpSuffix;
        } catch (IOException ignore) {
            // 非本地环境没有该文件
            throw new RuntimeException(String.format("开发本地环境没有该文件，请检查：%s", FeignConstants.DYNAMIC_SERVICE_NAME_PROPERTIES));
        }
    }

}
