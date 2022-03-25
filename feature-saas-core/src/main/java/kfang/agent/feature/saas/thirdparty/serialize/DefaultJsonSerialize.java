package kfang.agent.feature.saas.thirdparty.serialize;

import cn.hyugatool.json.JsonUtil;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;

/**
 * JsonSerialize
 *
 * @author pengqinglong
 * @since 2022/3/24
 */
public class DefaultJsonSerialize<T extends ThirdpartyForm> implements ThirdpartySerialize<T, String>{

    @Override
    public String serialize(T t) {
        return JsonUtil.toJsonString(t);
    }

}