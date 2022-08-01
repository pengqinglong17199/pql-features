package kfang.agent.feature.saas.thirdparty.serialize;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.collection.MapUtil;
import cn.hyugatool.http.NameValuePairUtil;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;
import kfang.agent.feature.saas.thirdparty.exception.ThirdpartyRequestException;
import org.apache.http.NameValuePair;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ParamSerialize
 *
 * @author pengqinglong
 * @since 2022/3/24
 */
public class DefaultParamSerialize<T extends ThirdpartyForm> implements ThirdpartySerialize<T, List<NameValuePair>> {

    @Override
    public List<NameValuePair> serialize(T t) {

        Map<String, Object> map = MapUtil.newHashMap();

        // 获取子类与父类的字段
        List<Field> list = ListUtil.newArrayList();
        list.addAll(Arrays.asList(t.getClass().getDeclaredFields()));
        list.addAll(Arrays.asList(t.getClass().getSuperclass().getDeclaredFields()));

        for (Field field : list) {
            field.setAccessible(true);

            try {
                map.put(field.getName(), field.get(t));
            } catch (IllegalAccessException e) {
                throw new ThirdpartyRequestException("字段异常");
            }
        }

        return NameValuePairUtil.ofObject(map);
    }

}