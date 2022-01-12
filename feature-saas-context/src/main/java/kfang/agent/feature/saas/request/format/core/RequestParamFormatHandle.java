package kfang.agent.feature.saas.request.format.core;

import cn.hyugatool.core.collection.MapUtil;
import kfang.agent.feature.saas.request.format.core.impl.StringFormatHandle;
import kfang.agent.feature.saas.request.format.anntations.StringFormat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * 请求参数格式化对外处理器
 *
 * @author pengqinglong
 * @since 2021/11/15
 */
public final class RequestParamFormatHandle {

    private static final Map<Class<? extends Annotation>, FormatHandle> MAPPINGS = MapUtil.newHashMap();

    static {
        MAPPINGS.put(StringFormat.class, new StringFormatHandle());
    }

    public static void handle(Annotation annotation, Field field, Object obj) throws Exception {
        FormatHandle formatHandle = MAPPINGS.get(annotation.annotationType());
        formatHandle.handle(annotation, field, obj);
    }
}