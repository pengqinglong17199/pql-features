package kfang.agent.feature.saas.parse.enums;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import kfang.infra.common.spring.SpringBeanPicker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * NacosConfigJsonEnumParse
 *
 * @author pengqinglong
 * @since 2022/2/16
 */
@Slf4j
public abstract class NacosConfigJsonEnumParse implements EnumParse<String, String>{

    /**
     * nacos配置文件
     */
    private static ConfigService CONFIG_SERVICE;

    static{
        try {
            AbstractApplicationContext applicationContext = SpringBeanPicker.getApplicationContext();
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            Properties properties = new Properties();
            properties.setProperty(PropertyKeyConst.SERVER_ADDR, environment.getProperty("spring.cloud.nacos.config.server-addr"));
            properties.setProperty(PropertyKeyConst.NAMESPACE, environment.getProperty("spring.cloud.nacos.config.namespace"));
            CONFIG_SERVICE = NacosFactory.createConfigService(properties);

        }catch (NacosException e){
            e.printStackTrace();
        }
    }

    /**
     * 原始配置数据
     */
    private String config;

    public NacosConfigJsonEnumParse(String dataId, String groupId) throws NacosException {
        this.initConfig(dataId, groupId);

        CONFIG_SERVICE.addListener(dataId, groupId, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                initAfter(configInfo);
            }
        });

    }

    public void initConfig(String dataId, String groupId){
        try {
            config = CONFIG_SERVICE.getConfig(dataId, groupId, 10000);

            this.initAfter(config);
        }catch (NacosException e){
            log.error("nacos获取配置异常", e);
        }
    }

    /**
     * 获取到config的后置操作 钩子函数 丢给子类实现
     */
    protected abstract void initAfter(String config);

}