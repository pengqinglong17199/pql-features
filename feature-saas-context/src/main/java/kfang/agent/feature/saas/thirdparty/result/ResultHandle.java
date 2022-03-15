package kfang.agent.feature.saas.thirdparty.result;

import cn.hyugatool.core.collection.MapUtil;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.json.JsonUtil;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;
import kfang.infra.common.spring.SpringBeanPicker;
import org.springframework.context.support.AbstractApplicationContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Handler;

/**
 * 结果处理器
 *
 * @author pengqinglong
 * @since 2022/3/15
 */
public interface ResultHandle<T extends ThirdpartyForm> {

    <K> K handle(String result, Class<K> resultClass);

    /**
     * 获取处理模块的class
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


