package kfang.agent.feature.dingtalk;

import lombok.Data;

import java.util.List;


/**
 * 发送钉钉消息 入参
 *
 * @author hyuga
 * @since 2022-01-19 11:11:53
 */
@Data
public class SendMessageIn {

    /**
     * 消息类型
     */
    private String msgType;
    /**
     * webhook
     */
    private String webhook;
    /**
     * 密钥
     */
    private String secret;
    /**
     * 文本
     */
    private String text;
    /**
     * 指定对象
     */
    private List<String> mobileList;
    /**
     * 是否推送所有人
     */
    private boolean isAtAll;

}