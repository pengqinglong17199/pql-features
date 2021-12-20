package cn.hyuga.feature.wechat.enterprise.data;

import cn.hyuga.feature.wechat.enterprise.base.AbstractWechatData;
import cn.hyugatool.core.collection.MapUtil;
import cn.hyugatool.json.JsonUtil;

import java.util.Map;

/**
 * WeChatTextData
 *
 * @author hyuga
 * @date 2019-12-27 16:16
 */
public class WechatTextData extends AbstractWechatData {

    /**
     * 实际接收map类型数据
     */
    private String text;

    public WechatTextData() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * 创建微信发送请求post数据 touser发送消息接收者 ，msgtype消息类型（文本/图片等）， application_id应用编号。
     * 本方法适用于text型微信消息，contentKey和contentValue只能组一对
     *
     * @return 创建发送消息
     */
    @Override
    public String createRequestJsonBody(long applicationId) {
        Map<Object, Object> wechatData = MapUtil.newHashMap();
        wechatData.put("touser", super.getReceiverMemberAccount());
        wechatData.put("msgtype", "text");
        wechatData.put("safe", 0);
        wechatData.put("agentid", applicationId);
        wechatData.put("text", MapUtil.newHashMap("content", this.text));

        return JsonUtil.toJsonString(wechatData);
    }

}
