package cn.hyuga.feature.wechat.enterprise.base;

/**
 * AbstractWeChatData
 *
 * @author hyuga
 * @date 2019-12-27 16:16
 */
public abstract class AbstractWechatData {

    /**
     * 成员账号（接收者的企业微信成员账号）
     */
    private String receiverMemberAccount;

    /**
     * 令牌
     * 由于时效性，建议令牌自行缓存，失效时间建议为2分钟
     */
    private String token;

    /**
     * 创建请求内容Json
     *
     * @param applicationId 应用ID
     * @return jsonBody
     */
    public abstract String createRequestJsonBody(long applicationId);

    public String getReceiverMemberAccount() {
        return receiverMemberAccount;
    }

    public void setReceiverMemberAccount(String receiverMemberAccount) {
        this.receiverMemberAccount = receiverMemberAccount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
