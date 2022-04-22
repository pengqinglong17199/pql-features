package kfang.agent.feature.saas.thirdparty.delegate;

import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.string.StringUtil;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;
import kfang.agent.feature.saas.thirdparty.factory.SerializeHandle;
import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * json序列化参数进行请求
 *
 * @author pengqinglong
 * @since 2022/3/15
 */
public class JsonRequestDelegate implements RequestDelegate {

    @Override
    public String post(String url, ThirdpartyForm form) {
        // 创建request
        HttpPost httpPost = (HttpPost) this.buildHttpRequest(url, form);
        httpPost.setHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

        // 封装请求参数
        String json = SerializeHandle.serialize(form);
        httpPost.setEntity(new StringEntity(json, Consts.UTF_8));
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

    @Override
    public String get(String url, ThirdpartyForm form) {
        // 封装请求参数
        // String param = this.packGetParam(form);

        String param = ObjectUtil.toUrlParams(form);

        // 创建request
        HttpGet httpGet = (HttpGet) this.buildHttpRequest(url + "?" + param, form);

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpGet);
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String packGetParam(ThirdpartyForm form) {
        StringBuilder sb = new StringBuilder("?");
        for (Field field : form.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object o = field.get(form);
                if (o != null) {
                    sb.append(field.getName());
                    sb.append("=");
                    sb.append(o);
                    sb.append("&");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return StringUtil.removeEnd(sb.toString(), "&");
    }

}