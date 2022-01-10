package kfang.agent.feature.saas.request.format.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 格式化处理器接口
 *
 * @author pengqinglong
 * @since 2021/11/15
 */
public interface FormatHandle {

    /**
     * 处理方法
     *
     * @param annotation 注解
     * @param field      字段
     * @param obj        对象
     * @throws Exception 方法中出现非意料的异常请 实现类 自身内部消化
     *                   不允许因为参数处理的非意料的异常导致接口响应失败
     *                   只有意料中的异常才可抛出
     */
    void handle(Annotation annotation, Field field, Object obj) throws Exception;
}