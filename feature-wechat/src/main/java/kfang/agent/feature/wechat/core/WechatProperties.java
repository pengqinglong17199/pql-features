package kfang.agent.feature.wechat.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ConfigFactory
 *
 * @author hyuga0410
 * @date 2019-04-16
 */
@ConfigurationProperties(prefix = WechatProperties.PREFIX)
public class WechatProperties {

    static final String PREFIX = "hyuga.wechat";

    // 企业微信相关=============================================

    private boolean useEnterpriseWeChatServices = true;

    /**
     * 企业微信ID
     */
    private String enterpriseId;

    /**
     * 企业微信应用ID
     */
    private long applicationId;

    /**
     * 企业微信应用密钥
     */
    private String applicationSecret;

    // 公众号服务号相关=============================================

    private boolean useWeChatServiceNoServices = true;

    /**
     * 服务号应用ID
     */
    private String serviceApplicationId;

    /**
     * 服务号应用密钥
     */
    private String serviceApplicationSecret;

    /**
     * 服务号令牌
     */
    private String serviceToken;

    /**
     * 服务号消息加解密密钥
     */
    private String serviceEncodingAesKey;

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getApplicationSecret() {
        return applicationSecret;
    }

    public void setApplicationSecret(String applicationSecret) {
        this.applicationSecret = applicationSecret;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public String getServiceApplicationId() {
        return this.serviceApplicationId;
    }

    public void setServiceApplicationId(String serviceApplicationId) {
        this.serviceApplicationId = serviceApplicationId;
    }

    public String getServiceApplicationSecret() {
        return this.serviceApplicationSecret;
    }

    public void setServiceApplicationSecret(String serviceApplicationSecret) {
        this.serviceApplicationSecret = serviceApplicationSecret;
    }

    public boolean isUseEnterpriseWeChatServices() {
        return this.useEnterpriseWeChatServices;
    }

    public void setUseEnterpriseWeChatServices(boolean useEnterpriseWeChatServices) {
        this.useEnterpriseWeChatServices = useEnterpriseWeChatServices;
    }

    public boolean isUseWeChatServiceNoServices() {
        return this.useWeChatServiceNoServices;
    }

    public void setUseWeChatServiceNoServices(boolean useWeChatServiceNoServices) {
        this.useWeChatServiceNoServices = useWeChatServiceNoServices;
    }

    public String getServiceEncodingAesKey() {
        return this.serviceEncodingAesKey;
    }

    public void setServiceEncodingAesKey(String serviceEncodingAesKey) {
        this.serviceEncodingAesKey = serviceEncodingAesKey;
    }

    public String getServiceToken() {
        return this.serviceToken;
    }

    public void setServiceToken(String serviceToken) {
        this.serviceToken = serviceToken;
    }
}
