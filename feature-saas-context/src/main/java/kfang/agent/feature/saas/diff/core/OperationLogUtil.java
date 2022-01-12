package kfang.agent.feature.saas.diff.core;


import kfang.agent.feature.saas.diff.annotations.OperationLog;

import java.util.Objects;

/**
 * 操作记录工具类
 *
 * @author pengqinglong
 * @since 2021/3/26
 */
public class OperationLogUtil {

    /**
     * 获取操作记录content
     *
     * @param nowEntity 新对象
     * @param history   旧对象（新增的时候可以为null）
     * @param clazz     模版class（字段要求与新旧对象保持一致）
     * @return 拼接好的字符串
     * @throws Exception Exception
     */
    public static String getContent(Object nowEntity, Object history, Class<?> clazz) throws Exception {
        OperationLog operationLog = clazz.getAnnotation(OperationLog.class);
        if (Objects.isNull(operationLog)) {
            throw new RuntimeException("class没有@OperationLog注解");
        }

        OperationHandle operationHandle = operationLog.handle().getDeclaredConstructor().newInstance();
        return operationHandle.handle(nowEntity, history, clazz);
    }
}