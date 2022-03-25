package kfang.agent.feature.saas.thirdparty.delegate;

import cn.hyugatool.core.object.ObjectUtil;
import kfang.agent.feature.saas.thirdparty.annotations.RequestEntity;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;
import kfang.agent.feature.saas.thirdparty.enums.RequestMode;
import kfang.agent.feature.saas.thirdparty.enums.SerializeMode;

/**
 * 请求委托执行器
 *
 * @author pengqinglong
 * @since 2022/3/15
 */
public class RequestDelegateExecutor {

    /**
     * url请求执行
     */
    public static String execute(String url, ThirdpartyForm form) {
        // 后期优化进本地缓存
        Class<? extends ThirdpartyForm> aClass = form.getClass();
        RequestEntity annotation = aClass.getAnnotation(RequestEntity.class);

        SerializeMode serialize = annotation.serialize();
        RequestDelegate delegate = createDelegate(serialize);

        if (ObjectUtil.isNull(delegate)) {
            return null;
        }

        RequestMode mode = annotation.mode();
        if (RequestMode.POST == mode) {
            return delegate.post(url, form);
        }

        if (RequestMode.GET == mode) {
            return delegate.get(url, form);
        }
        return null;
    }

    /**
     * 根据序列化模式创建请求委托类
     *
     * @param serializeMode 序列化模式
     * @return RequestDelegate
     */
    private static RequestDelegate createDelegate(SerializeMode serializeMode) {

        if (SerializeMode.JSON == serializeMode) {
            return new JsonRequestDelegate();
        }

        if (SerializeMode.PARAM == serializeMode) {
            return new ParamRequestDelegate();
        }

        return null;
    }

}