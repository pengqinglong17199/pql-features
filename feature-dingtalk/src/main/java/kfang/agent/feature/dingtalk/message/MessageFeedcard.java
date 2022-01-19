package kfang.agent.feature.dingtalk.message;

import cn.hyugatool.core.collection.ListUtil;
import com.dingtalk.api.request.OapiRobotSendRequest;
import kfang.agent.feature.dingtalk.enums.MessageTypeEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author hyuga
 * @since 2022-01-19 14:12:52
 */
public class MessageFeedcard extends AbstractMessage {

    @Setter
    @Getter
    private List<MessageFeedCardItem> feedCardItems;

    public MessageFeedcard(CoreParameter coreParameter) {
        super(coreParameter);
    }

    @Override
    public OapiRobotSendRequest convert() {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(MessageTypeEnum.FEED_CARD.getType());
        OapiRobotSendRequest.Feedcard feedcard = new OapiRobotSendRequest.Feedcard();

        List<OapiRobotSendRequest.Links> feedcardLinks = ListUtil.newArrayList();
        ListUtil.optimize(feedCardItems).forEach(feedcardItem -> {
            OapiRobotSendRequest.Links link = new OapiRobotSendRequest.Links();
            link.setTitle(feedcardItem.getTitle());
            link.setMessageURL(feedcardItem.getMessageURL());
            link.setPicURL(feedcardItem.getPicURL());
            feedcardLinks.add(link);
        });

        feedcard.setLinks(feedcardLinks);
        request.setFeedCard(feedcard);
        setAtInfo(request);
        return request;
    }

    @Data
    public static class MessageFeedCardItem {
        /**
         * 单条信息文本
         */
        private String title;
        /**
         * 点击单条信息到跳转链接
         */
        private String messageURL;
        /**
         * 单条信息后面图片的URL
         */
        private String picURL;
    }

}
