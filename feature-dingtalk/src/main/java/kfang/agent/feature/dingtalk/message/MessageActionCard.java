package kfang.agent.feature.dingtalk.message;

import com.dingtalk.api.request.OapiRobotSendRequest;
import kfang.agent.feature.dingtalk.enums.MessageTypeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hyuga
 * @since 2022-01-19 14:12:52
 */
public class MessageActionCard extends AbstractMessage {

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
    /**
     * 单个按钮的标题
     * 注意 设置此项和singleURL后，btns无效
     */
    @Setter
    @Getter
    private String singleTitle;
    /**
     * 单个按钮的标题
     * 点击消息跳转的URL，打开方式如下：
     * - 移动端，在钉钉客户端内打开
     * - PC端
     * -- 默认侧边栏打开
     * -- 希望在外部浏览器打开，请参考消息链接说明
     */
    @Setter
    @Getter
    private String singleURL;
    /**
     * 0：按钮竖直排列
     * 1：按钮横向排列
     */
    @Setter
    @Getter
    private String btnOrientation;

    public MessageActionCard(CoreParameter coreParameter) {
        super(coreParameter);
    }

    @Override
    public OapiRobotSendRequest convert() {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(MessageTypeEnum.ACTION_CARD.getType());
        OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
        actionCard.setTitle(this.title);
        actionCard.setText(this.text);
        actionCard.setSingleTitle(this.singleTitle);
        actionCard.setSingleURL(this.singleURL);
        request.setActionCard(actionCard);
        setAtInfo(request);
        return request;
    }


}
