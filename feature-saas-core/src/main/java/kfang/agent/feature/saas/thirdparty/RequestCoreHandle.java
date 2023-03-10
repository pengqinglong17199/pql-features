package kfang.agent.feature.saas.thirdparty;

import kfang.agent.feature.saas.thirdparty.annotations.RequestEntity;
import kfang.agent.feature.saas.thirdparty.authentication.ThirdpartyAuthentication;
import kfang.agent.feature.saas.thirdparty.config.ThirdpartyConfig;
import kfang.agent.feature.saas.thirdparty.delegate.RequestDelegateExecutor;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;
import kfang.agent.feature.saas.thirdparty.factory.ResultHandleParse;
import kfang.agent.feature.saas.thirdparty.factory.ThirdpartyAuthenticationFactory;

/**
 * 第三方请求核心处理类
 *
 * @author pengqinglong
 * @since 2022/3/15
 */
public class RequestCoreHandle {

    /**
     * 处理方法
     *
     * @param form  请求参数form
     * @param clazz 返回对象Class
     * @param <T>   返回泛形
     */
    public static <T> T request(ThirdpartyForm form, Class<T> clazz) {
        ThirdpartyAuthentication<ThirdpartyConfig, ThirdpartyForm> authentication = ThirdpartyAuthenticationFactory.getAuthentication(form);

        ThirdpartyConfig config = authentication.getConfig();

        // 鉴权器对form进行鉴权封装
        form = authentication.initForm(form);

        // 获取url与path拼接出接口请求地址
        RequestEntity annotation = form.getClass().getAnnotation(RequestEntity.class);
        String url = config.getUrl() + annotation.path();

        // 使用请求代理器 请求
        String result = RequestDelegateExecutor.execute(url, form);

        // 请求返回 通过结果解析器对结果进行解析
        return ResultHandleParse.parse(result, form, clazz);
    }

}