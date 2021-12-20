package cn.hyuga.feature.wechat.enterprise.data;

import cn.hyuga.feature.wechat.enterprise.base.AbstractWechatData;
import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.collection.MapUtil;
import cn.hyugatool.json.JsonUtil;

import java.util.List;
import java.util.Map;

/**
 * WeChatNewsData
 *
 * @author hyuga
 * @date 2019-12-27 16:16
 */
public class WechatNewsData extends AbstractWechatData {

    private String title;
    private String description;
    private String url;
    private String picurl;

    public WechatNewsData() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    @Override
    public String createRequestJsonBody(long applicationId) {
        Map<Object, Object> wechatData = MapUtil.newHashMap();
        wechatData.put("touser", super.getReceiverMemberAccount());
        wechatData.put("msgtype", "news");
        wechatData.put("agentid", applicationId);

        WechatArticle wechatArticle = new WechatArticle();
        wechatArticle.setTitle(this.title);
        wechatArticle.setDescription(this.description);
        wechatArticle.setUrl(this.url);
        wechatArticle.setPicurl(this.picurl);

        List<WechatArticle> articles = ListUtil.newArrayList();
        articles.add(wechatArticle);
        wechatData.put("news", MapUtil.newHashMap("articles", articles));
        System.out.println(JsonUtil.toJsonString(wechatData));
        return JsonUtil.toJsonString(wechatData);
    }

    static class WechatArticle {
        private String title;
        private String description;
        private String url;
        private String picurl;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPicurl() {
            return picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }
    }

}


