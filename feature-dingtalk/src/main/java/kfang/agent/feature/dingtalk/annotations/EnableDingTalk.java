package kfang.agent.feature.dingtalk.annotations;

import kfang.agent.feature.dingtalk.config.DingTalkConfiguration;
import kfang.agent.feature.dingtalk.core.AgentDingTalkCore;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 打开钉钉通知
 *
 * @author pengqinglong
 * @since 2022/1/21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DingTalkConfiguration.class, AgentDingTalkCore.class})
public @interface EnableDingTalk {
}