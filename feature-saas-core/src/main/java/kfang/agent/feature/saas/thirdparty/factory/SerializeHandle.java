package kfang.agent.feature.saas.thirdparty.factory;

import cn.hyugatool.core.collection.MapUtil;
import cn.hyugatool.core.object.ObjectUtil;
import kfang.agent.feature.saas.thirdparty.annotations.RequestEntity;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;
import kfang.agent.feature.saas.thirdparty.enums.SerializeMode;
import kfang.agent.feature.saas.thirdparty.serialize.DefaultJsonSerialize;
import kfang.agent.feature.saas.thirdparty.serialize.DefaultParamSerialize;
import kfang.agent.feature.saas.thirdparty.serialize.ThirdpartySerialize;
import kfang.infra.common.spring.SpringBeanPicker;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Map;

/**
 * 序列化处理器
 *
 * @author pengqinglong
 * @since 2022/3/24
 */
public class SerializeHandle {

    private static final Map<Class<? extends ThirdpartyForm>, ThirdpartySerialize<ThirdpartyForm, ?>> HANDLE_MAPPING;

    static {
        HANDLE_MAPPING = MapUtil.newHashMap();

        AbstractApplicationContext applicationContext = SpringBeanPicker.getApplicationContext();
        String[] beanNames = applicationContext.getBeanNamesForType(ThirdpartySerialize.class);

        for (String beanName : beanNames) {
            ThirdpartySerialize<ThirdpartyForm, Object> bean = ObjectUtil.cast(applicationContext.getBean(beanName));
            Class<? extends ThirdpartyForm> modelClass = bean.getModelClass();
            HANDLE_MAPPING.put(modelClass, bean);
        }
    }

    /**
     * 解析返回结果
     */
    public static <T> T serialize(ThirdpartyForm form){

        // 优先使用form的自定义的处理器进行处理
        ThirdpartySerialize<ThirdpartyForm, ?> handle = HANDLE_MAPPING.get(form.getClass());

        // form没有自定义处理器再寻找模块的自定义处理器进行处理
        if(handle == null){
            handle = HANDLE_MAPPING.get(form.getClass().getSuperclass());
        }

        // 模块也没有自定义处理器 则通过序列化模式寻找默认处理器
        if(handle == null) {
            RequestEntity annotation = form.getClass().getAnnotation(RequestEntity.class);
            SerializeMode serialize = annotation.serialize();
            if (SerializeMode.JSON == serialize) {
                handle = new DefaultJsonSerialize();
            } else {
                handle = new DefaultParamSerialize();
            }
        }

        // 处理返回结果
        Object serialize = handle.serialize(form);
        return ObjectUtil.cast(serialize);
    }
}