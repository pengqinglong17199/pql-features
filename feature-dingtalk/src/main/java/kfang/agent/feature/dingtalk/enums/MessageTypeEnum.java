package kfang.agent.feature.dingtalk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型
 *
 * @author hyuga
 * @since 2022-01-19 14:17:48
 */
@AllArgsConstructor
@Getter
public enum MessageTypeEnum {
    /**
     *
     */
    TEXT("text"),
    LINK("link"),
    MARKDOWN("markdown"),
    ACTION_CARD("actionCard"),
    FEED_CARD("feedCard"),
    ;

    private final String type;

}