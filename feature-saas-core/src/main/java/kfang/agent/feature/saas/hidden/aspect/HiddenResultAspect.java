package kfang.agent.feature.saas.hidden.aspect;

import cn.hyugatool.aop.aspectj.AspectInject;
import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.instance.ReflectionUtil;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.string.SensitiveUtil;
import cn.hyugatool.core.string.StringUtil;
import kfang.agent.feature.saas.hidden.anntations.HiddenFieldSensitiveInfo;
import kfang.infra.common.model.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * HousePermissionAspect
 *
 * @author pengqinglong
 * Create on 2021-04-10
 */
@Slf4j
@Aspect
@Component
public class HiddenResultAspect implements AspectInject {

    @Override
    @Pointcut("@annotation(kfang.agent.feature.saas.hidden.anntations.HiddenResultSensitiveInfo)")
    public void pointcut() {

    }

    private final List<Class<?>> BASE_TYPES
            = List.of(Date.class, Byte.class, Short.class, Integer.class, String.class, Long.class, Float.class, Double.class, Boolean.class, BigDecimal.class);

    @Override
    public void before(JoinPoint joinPoint) {

    }

    @Override
    public void afterReturning(JoinPoint joinPoint, Object result) throws Throwable {
        if (ObjectUtil.isNull(result)) {
            return;
        }

        Class<?> clazz = result.getClass();

        // 处理分页对象
        if (Objects.equals(Pagination.class, clazz)) {
            result = ReflectionUtil.getFieldValue(result, "items");
            List<?> tempList = (List<?>) result;
            if (ListUtil.isEmpty(tempList)) {
                return;
            }
            clazz = tempList.get(0).getClass();
        }

        // 处理数据隐藏
        this.handleHidden(result, clazz);
    }

    @Override
    public void after(JoinPoint joinPoint) {

    }

    @Override
    public void afterThrowing(Throwable exception) {

    }

    /**
     * 处理对象数据隐藏 包含递归 单独写出来
     */
    private void handleHidden(Object result, Class<?> clazz) throws Throwable {
        // 空对象直接拜拜
        if (Objects.isNull(result)) {
            return;
        }

        if (result instanceof List<?> list) {
            // 处理对象集合
            for (Object obj : list) {
                this.handleHidden(obj, obj.getClass());
            }
            return;
        }

        // 获取所有需要隐藏的字段
        List<Field> fields = ReflectionUtil.getDeclaredFields(clazz);
        List<Field> hiddenFieldList = ListUtil.optimize(fields).stream()
                .filter(field -> field.isAnnotationPresent(HiddenFieldSensitiveInfo.class)).collect(Collectors.toList());

        for (Field field : hiddenFieldList) {
            field.setAccessible(true);
            Class<?> fieldClass = ReflectionUtil.getFieldClass(field);

            if (!BASE_TYPES.contains(fieldClass)) {
                // 非基础数据类型 进行递归获取其字段
                handleHidden(field.get(result), fieldClass);
                continue;
            }

            if (Objects.equals(List.class, field.getType())) {
                // 处理字符串集合
                List<?> list = (List<?>) field.get(result);
                if (ListUtil.isEmpty(list)) {
                    continue;
                }

                // 全新的list字符串集合
                List<String> hiddenStrList = ListUtil.newArrayList(list.size());
                field.set(result, hiddenStrList);

                // 处理数据
                HiddenFieldSensitiveInfo annotation = field.getAnnotation(HiddenFieldSensitiveInfo.class);
                for (Object obj : list) {
                    hiddenStrList.add(this.hiddenData(annotation, obj.toString()));
                }
            } else {
                this.hiddenData(result, field);
            }
        }
    }

    /**
     * 隐藏数据方法
     */
    private void hiddenData(Object result, Field field) throws IllegalAccessException {
        HiddenFieldSensitiveInfo annotation = field.getAnnotation(HiddenFieldSensitiveInfo.class);
        Object val = field.get(result);
        if (StringUtil.isEmpty(val)) {
            return;
        }
        String str = hiddenData(annotation, val.toString());
        field.set(result, str);
    }

    /**
     * 隐藏数据原始逻辑
     */
    private String hiddenData(HiddenFieldSensitiveInfo annotation, String val) {

        // 直接替换字符
        if (StringUtil.hasText(annotation.replaceStr())) {
            return annotation.replaceStr();
        }

        // 没有替换字符使用掩码
        return SensitiveUtil.encodeText(val, annotation.head(), annotation.tail(), annotation.mask(), annotation.maskSize());
    }

}


