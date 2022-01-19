package kfang.agent.feature.dingtalk.message;

import com.dingtalk.api.request.OapiRobotSendRequest;
import kfang.agent.feature.dingtalk.enums.MessageTypeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hyuga
 * @since 2022-01-19 14:12:52
 */
public class MessageMarkdown extends AbstractMessage {

    /**
     * 标题
     */
    @Setter
    @Getter
    private String title;
    /**
     * 内容
     */
    @Setter
    @Getter
    private String text;

    public MessageMarkdown(CoreParameter coreParameter) {
        super(coreParameter);
    }

    @Override
    public OapiRobotSendRequest convert() {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(MessageTypeEnum.MARKDOWN.getType());
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle(this.title);
        markdown.setText(this.text);
        request.setMarkdown(markdown);
        setAtInfo(request);
        return request;
    }


}
