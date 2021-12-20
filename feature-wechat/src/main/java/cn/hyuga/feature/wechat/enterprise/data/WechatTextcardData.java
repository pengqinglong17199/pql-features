package cn.hyuga.feature.wechat.enterprise.data;

import cn.hyuga.feature.wechat.enterprise.base.AbstractWechatData;
import cn.hyugatool.core.collection.MapUtil;
import cn.hyugatool.json.JsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * WeChatTextcardData
 *
 * @author hyuga
 * @date 2019-12-27 16:16
 */
public class WechatTextcardData extends AbstractWechatData {

    private String title;
    private String description;
    private String url;
    private String btntxt;

    public WechatTextcardData() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setBtntxt(String btntxt) {
        this.btntxt = btntxt;
    }

    @Override
    public String createRequestJsonBody(long applicationId) {
        Map<Object, Object> wechatData = MapUtil.newHashMap();
        wechatData.put("touser", super.getReceiverMemberAccount());
        wechatData.put("msgtype", "textcard");
        wechatData.put("agentid", applicationId);

        Map<Object, Object> wechatTextcard = new HashMap<>(5);
        wechatTextcard.put("title", this.title);
        wechatTextcard.put("description", this.description);
        wechatTextcard.put("url", this.url);
        wechatTextcard.put("btntxt", this.btntxt);

        wechatData.put("textcard", wechatTextcard);
        System.out.println(JsonUtil.toJsonString(wechatData));
        return JsonUtil.toJsonString(wechatData);
    }

}


