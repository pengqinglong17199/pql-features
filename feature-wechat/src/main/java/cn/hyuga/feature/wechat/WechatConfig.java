package cn.hyuga.feature.wechat;

import cn.hyuga.feature.wechat.core.WechatProperties;
import cn.hyuga.feature.wechat.enterprise.transmitter.WechatDataTransmitter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * WeChatConfig
 *
 * @author hyuga
 * @date 2019-12-30 09:58
 */
@EnableConfigurationProperties(WechatProperties.class)
public class WechatConfig {

    @Resource
    private WechatProperties wechatProperties;

    @Bean("wechatDataTransmitter")
    public WechatDataTransmitter wechatDataTransmitter() {
        if (wechatProperties.isUseEnterpriseWeChatServices()) {
            return new WechatDataTransmitter(wechatProperties);
        } else {
            return null;
        }
    }

}
