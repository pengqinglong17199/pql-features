package kfang.agent.feature.saas.thirdparty.authentication;

import cn.hyugatool.core.object.ObjectUtil;
import kfang.agent.feature.saas.thirdparty.config.ThirdpartyConfig;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 第三方请求鉴权器接口
 *
 * @author pengqinglong
 * @since 2022/3/14
 */
public interface ThirdpartyAuthentication <T extends ThirdpartyConfig, K extends ThirdpartyForm>{

    /**
     * 获取config对象
     */
    T getConfig();

    /**
     * 对请求form进行封装
     */
    K initForm(K k);

    /**
     * 获取鉴权class
     *
     * @return class对象
     */
    default Class<K> getAuthenticationClass() {
        Type actualTypeArgument = ((ParameterizedType) this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
        try {
            return ObjectUtil.cast(Class.forName(actualTypeArgument.getTypeName()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}