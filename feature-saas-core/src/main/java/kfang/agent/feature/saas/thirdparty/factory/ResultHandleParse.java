package kfang.agent.feature.saas.thirdparty.factory;

import cn.hyugatool.core.collection.MapUtil;
import cn.hyugatool.core.object.ObjectUtil;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;
import kfang.agent.feature.saas.thirdparty.result.DefaultJsonResultHandle;
import kfang.agent.feature.saas.thirdparty.result.ResultHandle;
import kfang.infra.common.spring.SpringBeanPicker;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Map;

/**
 * ResultHandle
 *
 * @author pengqinglong
 * @since 2022/3/15
 */
public class ResultHandleParse {

    private static final Map<Class<? extends ThirdpartyForm>, ResultHandle<ThirdpartyForm>> HANDLE_MAPPING;

    static {
        HANDLE_MAPPING = MapUtil.newHashMap();

        AbstractApplicationContext applicationContext = SpringBeanPicker.getApplicationContext();
        String[] beanNames = applicationContext.getBeanNamesForType(ResultHandle.class);

        for (String beanName : beanNames) {
            ResultHandle<ThirdpartyForm> bean = ObjectUtil.cast(applicationContext.getBean(beanName));
            Class<? extends ThirdpartyForm> modelClass = bean.getModelClass();
            HANDLE_MAPPING.put(modelClass, bean);
        }
    }

    /**
     * 解析返回结果
     */
    public static <T> T parse(String result, ThirdpartyForm form, Class<T> clazz) {
        // 优先使用form的自定义的处理器进行处理
        ResultHandle<ThirdpartyForm> handle = HANDLE_MAPPING.get(form.getClass());

        // form没有自定义处理器再寻找模块的自定义处理器进行处理
        if (handle == null) {
            handle = HANDLE_MAPPING.get(form.getClass().getSuperclass());
        }

        // 模块也没有自定义处理器 则使用默认的json处理器
        if (handle == null) {
            handle = new DefaultJsonResultHandle();
        }

        // 处理返回结果
        return handle.handle(result, clazz);
    }

}