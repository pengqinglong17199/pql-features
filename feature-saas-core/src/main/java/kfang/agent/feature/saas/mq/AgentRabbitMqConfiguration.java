package kfang.agent.feature.saas.mq;

import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.system.NetworkUtil;
import cn.hyugatool.system.SystemUtil;
import kfang.agent.feature.saas.constants.FeignConstants;
import kfang.agent.feature.saas.constants.SaasConstants;
import kfang.infra.common.KfangInfraCommonProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * RabbitPropertiesConfig
 *
 * @author hyuga
 * @since 2020-02-21 02:55
 */
@Configuration
public class AgentRabbitMqConfiguration {

    public static final String RABBIT_CONFIG = "rabbitConfig";
    public static final String SERVER_NAME = "serverName";
    private static final String AUTO_DELETE = "autoDelete";

    @Resource
    private KfangInfraCommonProperties kfangInfraCommonProperties;

    @Bean(RABBIT_CONFIG)
    public Map<String, String> rabbitConfig() {
        String deploy = kfangInfraCommonProperties.getEnv().getDeploy();
        Map<String, String> configMap = new HashMap<>(2);
        if (SaasConstants.DEV.equals(deploy) && FeignConstants.isDeveloperLocalEnvironment(NetworkUtil.getLocalIpAddr())) {
            String tag = SystemUtil.getLocalHostName();
            // 用于隔离测试环境多节点MQ调用
            configMap.put(SERVER_NAME, "_" + tag);
            configMap.put(AUTO_DELETE, String.valueOf(true));
        } else {
            configMap.put(SERVER_NAME, StringUtil.EMPTY);
            configMap.put(AUTO_DELETE, String.valueOf(false));
        }
        return configMap;
    }

}