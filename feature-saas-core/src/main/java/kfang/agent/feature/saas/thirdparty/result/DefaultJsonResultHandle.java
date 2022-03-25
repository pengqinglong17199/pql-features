package kfang.agent.feature.saas.thirdparty.result;

import cn.hyugatool.json.JsonUtil;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;

/**
 * 默认的json结果处理器
 *
 * @author pengqinglong
 * @since 2022/3/15
 */
public class DefaultJsonResultHandle implements ResultHandle<ThirdpartyForm> {

    @Override
    public <K> K handle(String result, Class<K> resultClass) {
        return JsonUtil.toJavaObject(result, resultClass);
    }

}