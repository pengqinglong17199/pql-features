package kfang.agent.feature.saas.thirdparty.annotations;

import kfang.agent.feature.saas.thirdparty.config.ThirdpartyConfig;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;
import kfang.agent.feature.saas.thirdparty.enums.AuthenticationMode;
import kfang.agent.feature.saas.thirdparty.enums.RequestMode;
import kfang.agent.feature.saas.thirdparty.enums.SerializeMode;

import java.lang.annotation.*;

/**
 * 第三方请求实体注解
 * 该注解直接打在{@link ThirdpartyForm}的子类实现上
 *
 * @author pengqinglong
 * @since 2022/3/14
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestEntity {

    /**
     * 接口路径 拼接上 {@link ThirdpartyConfig#getUrl()}后形成完整的请求路径
     */
    String path();

    /**
     * 请求超时时间 单位:毫秒 默认10s
     * @return
     */
    int timeout() default 10000;

    /**
     * 请求方式 目前暂时支持 post和get 默认post
     */
    RequestMode mode() default RequestMode.POST;

    /**
     * 鉴权方式 目前暂时支持 header和body 默认header
     */
    AuthenticationMode authMode() default AuthenticationMode.HEADER;

    /**
     * 参数序列化方式 目前暂时支持 json和param 默认json
     */
    SerializeMode serialize() default SerializeMode.JSON;
}
