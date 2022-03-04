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
 * nacos配置json枚举解析器
 *
 * @author pengqinglong
 * @since 2022/2/16
 */
@Slf4j
public abstract class NacosConfigJsonEnumParse implements EnumParse {

    /**
     * nacos配置数据源
     */
    private static ConfigService CONFIG_SERVICE;

    private static final String SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR = "spring.cloud.nacos.config.server-addr";
    private static final String SPRING_CLOUD_NACOS_CONFIG_NAMESPACE = "spring.cloud.nacos.config.namespace";

    static {
        /*
         * 类第一次被加载进jvm时触发
         * 初始化nacos配置数据源
         */
        try {
            AbstractApplicationContext applicationContext = SpringBeanPicker.getApplicationContext();
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            Properties properties = new Properties();
            properties.setProperty(PropertyKeyConst.SERVER_ADDR, environment.getProperty(SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR));
            properties.setProperty(PropertyKeyConst.NAMESPACE, environment.getProperty(SPRING_CLOUD_NACOS_CONFIG_NAMESPACE));
            CONFIG_SERVICE = NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    /**
     * 原始配置数据 缓存原始数据 子类可以读取
     */
    protected String config;

    public NacosConfigJsonEnumParse(String dataId, String groupId) throws NacosException {

        this.initConfig(dataId, groupId);

        CONFIG_SERVICE.addListener(dataId, groupId, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                initConfig(dataId, groupId);
            }
        });

    }

    /**
     * 初始化原始数据
     *
     * @param dataId  dataId
     * @param groupId groupId
     */
    public final void initConfig(String dataId, String groupId) {
        try {
            config = CONFIG_SERVICE.getConfig(dataId, groupId, 10000);
            // 模版方法模式 子类实现
            this.initAfter(config);
        } catch (NacosException e) {
            log.error("nacos获取配置异常", e);
        }
    }

    /**
     * 获取到config的后置操作 钩子函数 丢给子类实现
     * (模版方法模式)
     *
     * @param config config
     */
    protected abstract void initAfter(String config);

}