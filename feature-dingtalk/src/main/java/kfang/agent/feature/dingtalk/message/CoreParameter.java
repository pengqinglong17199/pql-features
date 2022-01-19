package kfang.agent.feature.dingtalk.message;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author hyuga
 * @since 2022-01-19 15:20:19
 */
@Builder
@Getter
public class CoreParameter {

    /**
     * 访问令牌
     */
    private final String accessToken;

    /**
     * 密钥串，用于签名
     */
    private final String secret;

    /**
     * 当前时间戳
     */
    private final long timestamp = System.currentTimeMillis();

    /**
     * 指定对象(只有text有效)
     * Arrays.asList("132xxxxxxxx")
     */
    private final List<String> atMobiles;

    // /**
    //  * 指定对象(只有text有效)
    //  */
    // private final List<String> atUserIds;

    /**
     * 是否推送所有人(只有text和markdown有效)
     */
    private final boolean isAtAll;

}
