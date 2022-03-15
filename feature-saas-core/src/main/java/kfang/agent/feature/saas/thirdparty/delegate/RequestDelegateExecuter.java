package kfang.agent.feature.saas.thirdparty.delegate;

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
public class RequestDelegateExecuter {


    /**
     * 执行
     */
    public static String execute(String url, ThirdpartyForm form){
        // 后期优化进本地缓存
        Class<? extends ThirdpartyForm> aClass = form.getClass();
        RequestEntity annotation = aClass.getAnnotation(RequestEntity.class);

        SerializeMode serialize = annotation.serialize();
        RequestDelegate delegate = createDelegate(serialize);

        RequestMode mode = annotation.mode();
        if(RequestMode.POST == mode){
            return delegate.post(url, form);
        }

        if(RequestMode.GET == mode){
            return delegate.get(url, form);
        }
    }

    private static RequestDelegate createDelegate(SerializeMode serialize) {
        RequestDelegate delegate = null;
        
        if(SerializeMode.JSON == serialize){
            return new JsonRequestDelegate();
        }

        if(SerializeMode.PARAM == serialize){
            return new ParamRequestDelegate();
        }
    }
}