package kfang.agent.feature.saas.thirdparty.delegate;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.collection.MapUtil;
import cn.hyugatool.core.string.StringUtil;
import kfang.agent.feature.saas.thirdparty.annotations.AuthParam;
import kfang.agent.feature.saas.thirdparty.annotations.RequestEntity;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;
import kfang.agent.feature.saas.thirdparty.enums.AuthenticationMode;
import kfang.agent.feature.saas.thirdparty.enums.RequestMode;
import kfang.agent.feature.saas.thirdparty.exception.ThirdpartyRequestException;
import kfang.infra.web.common.util.HttpClientUtil;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 请求委托器
 *
 * @author pengqinglong
 * @since 2022/3/15
 */
public interface RequestDelegate {

    String post(String url, ThirdpartyForm form);

    String get(String url, ThirdpartyForm form);

    default HttpRequestBase buildHttpRequest(String url, ThirdpartyForm form){

        Class<? extends ThirdpartyForm> clazz = form.getClass();
        RequestEntity annotation = clazz.getAnnotation(RequestEntity.class);

        try {
            URIBuilder builder = new URIBuilder(url);
            HttpRequestBase httpRequest = null;
            if(annotation.mode() == RequestMode.GET){
                httpRequest = new HttpGet(builder.build());
            }else {
                httpRequest = new HttpPost(builder.build());
            }

            httpRequest.setConfig(RequestConfig.custom().setConnectionRequestTimeout(10000).setConnectTimeout(10000).setSocketTimeout(10000).build());

            // body跟随序列化直接进入body了 不用处理
            AuthenticationMode authenticationMode = annotation.authMode();
            if (authenticationMode == AuthenticationMode.BODY) {
                return httpRequest;
            }

            // 获取自己+父类的字段
            List<Field> fieldList = ListUtil.newArrayList(clazz.getDeclaredFields());
            fieldList.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));

            // 找出鉴权参数
            fieldList = fieldList.stream()
                    .filter(f -> f.isAnnotationPresent(AuthParam.class))
                    .collect(Collectors.toList());

            // 没有返回
            if (ListUtil.isEmpty(fieldList)) {
                return httpRequest;
            }

            // 封装请求头
            this.packHeader(form, httpRequest, fieldList);

            return httpRequest;
        }catch (Exception e){
            e.printStackTrace();
            throw new ThirdpartyRequestException(e.fillInStackTrace().toString() + Arrays.toString(e.getStackTrace()));
        }
    }

    private void packHeader(ThirdpartyForm form, HttpRequestBase httpRequest, List<Field> fieldList) {
        // 存在鉴权参数 开始封装
        for (Field field : fieldList) {
            field.setAccessible(true);
            // 获取鉴权字段名
            AuthParam authParam = field.getAnnotation(AuthParam.class);
            String key = StringUtil.hasText(authParam.value()) ? authParam.value() : field.getName();
            try {

                // 为空校验
                Object o = field.get(form);
                if (o == null) {
                    throw new ThirdpartyRequestException(String.format("entity:[%s] 鉴权参数:[%s]为空", form.getClass().getName(), key));
                }

                httpRequest.setHeader(key, o.toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}