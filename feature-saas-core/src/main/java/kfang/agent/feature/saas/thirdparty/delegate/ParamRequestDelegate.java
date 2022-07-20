package kfang.agent.feature.saas.thirdparty.delegate;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.collection.MapUtil;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.http.NameValuePairUtil;
import cn.hyugatool.json.JsonUtil;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 表单参数方式请求（暂未支持）
 *
 * @author pengqinglong
 * @since 2022/3/15
 */
public class ParamRequestDelegate implements RequestDelegate {

    @Override
    public String post(String url, ThirdpartyForm form) {

        // 创建request
        HttpPost httpPost = (HttpPost) this.buildHttpRequest(url, form);
        httpPost.setHeader(CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.toString());

        // 处理参数
        httpPost.setEntity(new UrlEncodedFormEntity(NameValuePairUtil.ofObject(form), StandardCharsets.UTF_8));

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpPost);
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}