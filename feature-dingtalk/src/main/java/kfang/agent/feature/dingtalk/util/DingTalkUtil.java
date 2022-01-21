package kfang.agent.feature.dingtalk.util;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.json.JsonUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import kfang.agent.feature.dingtalk.config.DingTalkConfiguration;
import kfang.agent.feature.dingtalk.enums.Author;
import kfang.agent.feature.dingtalk.message.AbstractMessage;
import kfang.agent.feature.dingtalk.message.CoreParameter;
import kfang.agent.feature.dingtalk.message.MessageText;
import kfang.infra.common.spring.SpringBeanPicker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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
    private static String getSign(long timestamp, String secret) {
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
    private static String getServerUrl(String accessToken, long timestamp, String secret) {
        String sign = getSign(timestamp, secret);
        return String.format(DingTalkConfiguration.SEND_URL, accessToken, timestamp, sign);
    }


    /**
     * 推送消息
     *
     * @param abstractMessage abstractMessage
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

    public static Build builder() {
        return new Build();
    }

    @Data
    public static class Build {

        private List<Author> authorList;

        private String title;

        private String context;

        private String secret;

        private String accessToken;

        public Build atMobile(Author author) {
            if (authorList == null) {
                authorList = ListUtil.newArrayList();
            }
            authorList.add(author);
            return this;
        }

        public Build atMobiles(Author... author) {
            if (authorList == null) {
                authorList = ListUtil.newArrayList();
            }
            authorList.addAll(ListUtil.optimize(author));
            return this;
        }

        public Build title(String title) {
            this.title = title;
            return this;
        }

        public Build context(String context) {
            this.context = context;
            return this;
        }

        public Build secret(String secret) {
            this.secret = secret;
            return this;
        }

        public Build accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        DingTalkConfiguration dingTalkConfiguration = SpringBeanPicker.getBean(DingTalkConfiguration.class);

        public boolean send() {
            CoreParameter coreParameter = CoreParameter.builder()
                    .accessToken(StringUtil.hasText(accessToken) ? accessToken : dingTalkConfiguration.getAccessToken())
                    .secret(StringUtil.hasText(secret) ? secret : dingTalkConfiguration.getSecret())
                    .atMobiles(ListUtil.flat(authorList, Author::getPhone))
                    .build();

            try {
                MessageText text = new MessageText(coreParameter);
                text.setText(String.format("%s \n\n %s", title, context));
                DingTalkUtil.sendMessage(text);
                return true;
            } catch (Exception e) {
                log.error("钉钉推送消息异常", e);
                return false;
            }
        }
    }

    // public static void main(String[] args) {
    //     String title = String.format("当前环境 : %s \n异常服务 : %s \n所属类名 : %s \n方法名称 : %s \n研发人员 : %s",
    //             "dev".toUpperCase() + " [" + NetworkUtil.getLocalIpAddr() + "]",
    //             "service-agent-house".toUpperCase(),
    //             "joinPoint.getSignature().getDeclaringTypeName()",
    //             "joinPoint.getSignature().getName()",
    //             Author.HUANG_ZE_YUAN.getName());
    //     String context = String.format("异常信息 : %s", "e.getMessage()");
    //     DingTalkUtil.builder()
    //             .accessToken("446cfbe11456505cf01ac32f26bf88748395b1761121d991105154134244e35b")
    //             .secret("SECc4c82c1fc0fc9e171682236c330c3245553bc18f887d1a215daf3aae1b6c479a")
    //             .atMobiles(Author.HUANG_ZE_YUAN)
    //             .title(title)
    //             .context(context)
    //             .send();
    //
    // }

}
