package kfang.agent.feature.saas.feign;

import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.core.uri.URLUtil;
import cn.hyugatool.system.NetworkUtil;
import cn.hyugatool.system.SystemUtil;
import feign.Feign;
import feign.Target;
import kfang.agent.feature.saas.constants.FeignConstants;
import kfang.agent.feature.saas.feign.enums.ServiceSignEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

/**
 * FeignBuilderHelper
 *
 * @author hyuga
 * @since 2022/01/07
 */
@Slf4j
public class FeignBuilderHelper extends Feign.Builder {

    /**
     * index1:uri
     * index2:suffix
     * index3:path
     */
    private static final String DYNAMIC_URL = "%s-%s%s";

    @Override
    public <T> T target(Target<T> target) {
        return super.target(new Target.HardCodedTarget<>(target.type(), target.name(), target.url()) {
            @SneakyThrows
            @Override
            public String url() {
                boolean isolation = AgentFeignConfiguration.isIsolation();
                if (!isolation) {
                    return super.url();
                }

                final String urlStr = super.url();
                final URL url = URLUtil.url(urlStr);
                final String uri = URLUtil.getHost(url).toString();
                final String path = url.getPath();

                String localIpAddr = NetworkUtil.getLocalIpAddr();

                boolean ipWhetherNeedIsolation = FeignConstants.ipWhetherNeedIsolation(localIpAddr);

                if (!ipWhetherNeedIsolation) {
                    // 请求IP不需要隔离
                    return super.url();
                }

                ServiceSignEnum serviceSignEnum = AgentFeignConfiguration.serviceSign();
                if (!urlStr.contains(serviceSignEnum.getSign())) {
                    // 请求url不是指定的服务标识不隔离
                    return super.url();
                }

                if (FeignConstants.isTestEnvironment(localIpAddr)) {
                    // 测试环境隔离
                    return String.format(DYNAMIC_URL, uri, FeignConstants.getSuffixOfTest(localIpAddr), path);
                }

                if (FeignConstants.isDeveloperLocalEnvironment(localIpAddr)) {
                    int isLocalEnvAndEnabled = ServiceNameSuffix.isEnabledOfLocalService(super.name());
                    if (isLocalEnvAndEnabled != 0 && isLocalEnvAndEnabled != 1) {
                        throw new RuntimeException(String.format("[%s]启用标识错误，只允许为0或1.", super.name()));
                    }
                    if (isLocalEnvAndEnabled == 0) {
                        String defaultEnvIpSuffix = ServiceNameSuffix.getDefaultEnvIpSuffix();
                        if (StringUtil.isEmpty(defaultEnvIpSuffix)) {
                            return super.url();
                        }
                        return String.format(DYNAMIC_URL, uri, defaultEnvIpSuffix, path);
                    } else {
                        // 开发环境隔离
                        return String.format(DYNAMIC_URL, uri, SystemUtil.getLocalHostName(), path);
                    }
                }
                return super.url();
            }
        });
    }

}
