package kfang.agent.feature.dingtalk.message;

import com.dingtalk.api.request.OapiRobotSendRequest;
import kfang.agent.feature.dingtalk.enums.MessageTypeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hyuga
 * @since 2022-01-19 14:12:52
 */
public class MessageText extends AbstractMessage {

    /**
     * 文本
     */
    @Getter
    @Setter
    private String text;

    public MessageText(CoreParameter coreParameter) {
        super(coreParameter);
    }

    @Override
    public OapiRobotSendRequest convert() {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(MessageTypeEnum.TEXT.getType());
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(this.text);
        request.setText(text);
        setAtInfo(request);
        return request;
    }

}
