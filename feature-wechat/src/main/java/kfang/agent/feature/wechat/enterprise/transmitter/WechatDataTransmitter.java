package kfang.agent.feature.wechat.enterprise.transmitter;

import kfang.agent.feature.wechat.core.WechatProperties;
import kfang.agent.feature.wechat.enterprise.base.AbstractWechatData;
import cn.hyugatool.core.constants.HyugaCharsets;
import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.http.HyugaHttp;
import cn.hyugatool.http.HyugaHttpSingleton;

/**
 * WeChatDataTransmitter
 *
 * @author hyuga
 * @date 2019-12-27 16:15
 */
public class WechatDataTransmitter {

    /**
     * 企业微信ID
     */
    private final String enterpriseId;

    /**
     * 企业微信应用ID
     */
    private final long applicationId;

    /**
     * 企业微信应用密钥
     */
    private final String applicationSecret;

    private static final HyugaHttp WECHAT_HTTP = HyugaHttpSingleton.init().charset(HyugaCharsets.UTF_8).needSSL(true).printLog(true);

    /**
     * 用于获取token的url
     */
    private static final String WECHAT_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
    /**
     * 用于发送消息请求的url
     */
    private static final String WECHAT_MESSAGE_SEND_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";


    public WechatDataTransmitter(WechatProperties wechatProperties) {
        enterpriseId = wechatProperties.getEnterpriseId();
        applicationId = wechatProperties.getApplicationId();
        applicationSecret = wechatProperties.getApplicationSecret();

        if (StringUtil.isEmpty(enterpriseId)) {
            throw new RuntimeException("企业ID不能为空");
        }
        if (StringUtil.isEmpty(applicationId)) {
            throw new RuntimeException("企业微信应用ID不能为空");
        }
        if (StringUtil.isEmpty(applicationSecret)) {
            throw new RuntimeException("企业微信应用密钥不能为空");
        }
    }

    public String send(AbstractWechatData wechatData) {
        String postData = wechatData.createRequestJsonBody(applicationId);
        String token = StringUtil.hasText(wechatData.getToken()) ? wechatData.getToken() : this.getAccessToken();
        String url = WECHAT_MESSAGE_SEND_URL + token;
        return WECHAT_HTTP.post(url, postData).getContent();
    }


    /**
     * 获取ToKen的请求
     */
    private String getTokenUrl() {
        return String.format(WECHAT_TOKEN_URL, enterpriseId, applicationSecret);
    }

    /**
     * 通过tokenUrl调用微信接口鉴权，获取鉴权令牌
     * 获取令牌有每日上限，如果频率不高无所高，否则令牌每次调用得缓存一分钟
     *
     * @return access_token
     */
    public String getAccessToken() {
        String tokenUrl = getTokenUrl();
        HyugaHttp.HttpResult httpResult = WECHAT_HTTP.get(tokenUrl);
        return httpResult.isOkAndHasContext().parsingObject("access_token").getString();
    }

}
