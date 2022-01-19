package kfang.agent.feature.dingtalk.message;

import cn.hyugatool.core.collection.ListUtil;
import com.dingtalk.api.request.OapiRobotSendRequest;
import lombok.Data;

import java.util.List;

/**
 * @author hyuga
 * @since 2022-01-19 15:59:33
 */
@Data
public abstract class AbstractMessage {

    private final CoreParameter coreParameter;

    protected AbstractMessage(CoreParameter coreParameter) {
        this.coreParameter = coreParameter;
    }

    /**
     * 将AbstractMessage子类转换为OapiRobotSendRequest
     *
     * @return OapiRobotSendRequest
     */
    public abstract OapiRobotSendRequest convert();

    protected void setAtInfo(OapiRobotSendRequest request) {
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        List<String> atMobiles = this.coreParameter.getAtMobiles();
        if (ListUtil.hasItem(atMobiles)) {
            at.setAtMobiles(atMobiles);
        }
        // List<String> atUserIds = this.coreParameter.getAtUserIds();
        // if (ListUtil.hasItem(atUserIds)) {
        //     at.setAtUserIds(atUserIds);
        // }
        boolean atAll = this.coreParameter.isAtAll();
        if (atAll) {
            // isAtAll类型如果不为Boolean，请升级至最新SDK
            at.setIsAtAll(true);
        }
        request.setAt(at);
    }

}
