package kfang.agent.feature.dingtalk.config;

import kfang.agent.feature.dingtalk.enums.Author;
import kfang.infra.common.KfangInfraCommonProperties;
import kfang.infra.common.spring.SpringBeanPicker;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * 钉钉相关配置文件
 *
 * @author pengqinglong
 * @since 2022/1/21
 */
@Configuration
public class DingTalkConfiguration {

    public static final String SEND_URL = "https://oapi.dingtalk.com/robot/send?access_token=%s&timestamp=%s&sign=%s";

    private static final String PATH = "ding-%s.properties";

    private static final String SECRET = "secret";

    private static final String ACCESS_TOKEN = "accessToken";

    private static KfangInfraCommonProperties kfangProperties = null;
    /**
     * 配置文件 后续可能拓展其他配置
     */
    private Properties properties = null;

    @Getter
    private String accessToken;

    @Getter
    private String secret;


    @PostConstruct
    public void init() throws IOException {

        properties = new Properties();

        kfangProperties = SpringBeanPicker.getBean(KfangInfraCommonProperties.class);
        String deploy = isDev() ? kfangProperties.getEnv().getDeploy() : "pro";

        InputStream resource = getClass().getClassLoader().getResourceAsStream(String.format(PATH, deploy));

        properties.load(resource);

        secret = properties.getProperty(SECRET);

        accessToken = properties.getProperty(ACCESS_TOKEN);
    }

    /**
     * 获取当前服务名
     */
    public static String getServiceName(){
        return kfangProperties.getEnv().getAppName();
    }

    /**
     * 获取当前环境
     */
    public static String getDeploy(){
        return kfangProperties.getEnv().getDeploy();
    }

    public static final Author[] PRO_AUTHORS = {Author.PENG_QING_LONG, Author.HUANG_ZHE_YUAN, Author.FANG_JIN_KUN};

    /**
     * 当前是否是开发/测试环境
     */
    public static boolean isDev(){
        return Objects.equals("dev", kfangProperties.getEnv().getDeploy());
    }
}