package kfang.agent.feature.saas.thirdparty.serialize;

import cn.hyugatool.core.object.ObjectUtil;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 第三方序列化器
 *
 * @author pengqinglong
 * @since 2022/3/24
 */
public interface ThirdpartySerialize<T extends ThirdpartyForm, K> {

    /**
     * 序列化参数
     *
     * @param t the t
     * @return the K
     */
    K serialize(T t);

    /**
     * 获取当前序列化器的模块class
     *
     * @return class对象
     */
    default Class<T> getModelClass() {
        Type actualTypeArgument = ((ParameterizedType) this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        try {
            return ObjectUtil.cast(Class.forName(actualTypeArgument.getTypeName()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}