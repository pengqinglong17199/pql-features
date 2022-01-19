package kfang.agent.feature.dingtalk.message;

import com.dingtalk.api.request.OapiRobotSendRequest;
import kfang.agent.feature.dingtalk.enums.MessageTypeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hyuga
 * @since 2022-01-19 14:12:52
 */
public class MessageLink extends AbstractMessage {

    /**
     * 消息跳转路径
     */
    @Setter
    @Getter
    private String messageUrl;
    /**
     * 图片路径
     */
    @Setter
    @Getter
    private String picUrl;
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

    public MessageLink(CoreParameter coreParameter) {
        super(coreParameter);
    }

    @Override
    public OapiRobotSendRequest convert() {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(MessageTypeEnum.LINK.getType());
        OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
        link.setMessageUrl(this.messageUrl);
        link.setPicUrl(this.picUrl);
        link.setTitle(this.title);
        link.setText(this.text);
        request.setLink(link);
        setAtInfo(request);
        return request;
    }


}
