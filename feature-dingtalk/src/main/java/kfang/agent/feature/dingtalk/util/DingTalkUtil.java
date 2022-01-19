package kfang.agent.feature.dingtalk.util;

import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.json.JsonUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import kfang.agent.feature.dingtalk.message.AbstractMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * DingTalk工具类
 *
 * @author hyuga
 * @since 2022-01-19 14:42:58
 */
@Slf4j
public final class DingTalkUtil {

    private static final String HMAC_SHA_256 = "HmacSHA256";

    /**
     * 根据时间戳和密钥串生成签名串
     *
     * @param timestamp 时间戳
     * @param secret    密钥串
     * @return 获取签名串
     */
    public static String getSign(long timestamp, String secret) {
        if (timestamp <= 0 || StringUtil.isEmpty(secret)) {
            throw new RuntimeException("参数错误");
        }
        String stringToSign = timestamp + "\n" + secret;
        String sign = null;
        try {
            Mac mac = Mac.getInstance(HMAC_SHA_256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        System.out.println(sign);
        return sign;
    }

    /**
     * 根据授权token、时间戳、密钥串，生成服务请求地址
     *
     * @param accessToken 授权token
     * @param timestamp   时间戳
     * @param secret      密钥串
     * @return 请求url
     */
    public static String getServerUrl(String accessToken, long timestamp, String secret) {
        String sign = getSign(timestamp, secret);
        String serverUrl = "https://oapi.dingtalk.com/robot/send?access_token=%s&timestamp=%s&sign=%s";
        return String.format(serverUrl, accessToken, timestamp, sign);
    }


    /**
     * @param abstractMessage
     * @description: 推送消息
     * @return: void
     * @author: zxq
     * @Date: 2020/11/26 13:28
     */
    public static void sendMessage(AbstractMessage abstractMessage) throws Exception {
        log.info("开始推送钉钉消息：" + JsonUtil.toJsonString(abstractMessage));

        String accessToken = abstractMessage.getCoreParameter().getAccessToken();
        String secret = abstractMessage.getCoreParameter().getSecret();
        long timestamp = abstractMessage.getCoreParameter().getTimestamp();

        String serverUrl = DingTalkUtil.getServerUrl(accessToken, timestamp, secret);
        DingTalkClient client = new DefaultDingTalkClient(serverUrl);
        OapiRobotSendRequest sendRequest = abstractMessage.convert();
        OapiRobotSendResponse response = client.execute(sendRequest);
        log.info("钉钉推送返回结果：");
        System.out.println(StringUtil.formatString(response));

    }

}
