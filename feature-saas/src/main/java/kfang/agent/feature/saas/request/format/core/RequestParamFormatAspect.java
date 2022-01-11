package kfang.agent.feature.saas.request.format.core;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.instance.ReflectionUtil;
import kfang.agent.feature.saas.request.format.anntations.StringFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 请求参数格式化切面
 *
 * @author pengqinglong
 * @since 2021/11/15
 */
@Slf4j
@Aspect
@Component
public class RequestParamFormatAspect {

    @Pointcut("execution(* com.kfang.web..*.controller..*.*(..))")
    private void cutMethod() {
    }

    /**
     * 看房的包头 用于排除非看房的类
     */
    public static final String KFANG_PACKAGE_NAME = "kfang";

    /**
     * 基础类的包名 找父类时跳过该包下的类 优化速度
     */
    private static final String BASIC_PACKAGE_NAME = "kfang.infra.api.validate";

    /**
     * 注解包名 用于兼容后续的注解处理
     */
    private static final String ANNOTATION_PACKAGE_NAME = "kfang.agent.feature.saas.request.format.anntations";

    /**
     * 前置通知：在目标方法执行前调用
     */
    @Before("cutMethod()")
    public void begin(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            Class<?> aClass = arg.getClass();

            // 循环获取所有字段包括父类的字段
            List<Field> fieldList = this.getFieldList(aClass);

            // 循环所有字段 找对应的策略处理自己
            this.handleFieldList(arg, fieldList);
        }
    }

    /**
     * 处理字段集合
     */
    private void handleFieldList(Object arg, List<Field> fieldList) throws Exception {
        for (Field field : fieldList) {

            // 获取字段的class
            Class<?> fieldClass = ReflectionUtil.getFieldClass(field);

            // 如果对象是集合且对象包包含看房的包名 递归去找对象字段处理
            if (field.getType() == List.class && fieldClass.getPackageName().contains(KFANG_PACKAGE_NAME)) {
                List<Field> recursionList = this.getFieldList(fieldClass);
                Object objList = field.get(arg);
                if (objList instanceof List) {
                    List list = (List) objList;
                    for (Object obj : ListUtil.optimize(list)) {
                        this.handleFieldList(obj, recursionList);
                    }
                }
                continue;
            }

            // 如果对象包包含看房的包名 递归去找对象字段处理
            if (fieldClass.getPackageName().contains(KFANG_PACKAGE_NAME)) {
                List<Field> recursionList = this.getFieldList(fieldClass);
                this.handleFieldList(field.get(arg), recursionList);
                continue;
            }

            // 处理逻辑
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (this.checkPackageName(annotation.annotationType(), ANNOTATION_PACKAGE_NAME)) {
                    RequestParamFormatHandle.handle(annotation, field, arg);
                }
            }
        }
    }

    /**
     * 获取class的字段集合
     */
    private List<Field> getFieldList(Class<?> aClass) {

        List<Field> fieldList = ListUtil.newArrayList();

        while (aClass != null && !this.checkPackageName(aClass, BASIC_PACKAGE_NAME)) {

            // 获取所有有注解的字段
            fieldList.addAll(Arrays.stream(aClass.getDeclaredFields())
                    .filter(field -> Arrays.stream(field.getDeclaredAnnotations()).
                            anyMatch(annotation -> this.checkPackageName(annotation.annotationType(), ANNOTATION_PACKAGE_NAME)))
                    .collect(Collectors.toList()));

            // 自下而上找父类
            aClass = aClass.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 检查class的包名
     */
    private boolean checkPackageName(Class<?> aClass, String annotationPackageName) {
        return aClass.getPackageName().contains(annotationPackageName);
    }

    public static void main(String[] args) throws Exception {

        TestDemo demo = new TestDemo();
        demo.setHouseId("   houseId  👴  TestDemo   ");

        TestDemoObj test1 = new TestDemoObj();
        test1.setGardenId("  你要买什么新房呀   ");
        demo.setObj(test1);

        TestDemoList test2 = new TestDemoList();
        test2.setUnitId("     ");
        TestDemoList test3 = new TestDemoList();
        test3.setUnitId("  test 3   ");
        TestDemoList test4 = new TestDemoList();
        test4.setUnitId(null);
        List<TestDemoList> list = ListUtil.newArrayList();
        list.add(test2);
        list.add(test3);
        list.add(test4);
        demo.setList(list);

        RequestParamFormatAspect requestParamFormatAspect = new RequestParamFormatAspect();
        Class<?> aClass = demo.getClass();
        // 循环获取所有字段包括父类的字段
        List<Field> fieldList = requestParamFormatAspect.getFieldList(aClass);

        // 循环所有字段 找对应的策略处理自己
        requestParamFormatAspect.handleFieldList(demo, fieldList);

        System.out.println(demo);
    }

    @Data
    public static class TestDemo {

        @StringFormat(emoji = true)
        private String houseId;

        @StringFormat
        private TestDemoObj obj;

        @StringFormat
        private List<TestDemoList> list;

    }

    @Data
    public static class TestDemoObj {

        @StringFormat(rear = false)
        private String gardenId;
    }

    @Data
    public static class TestDemoList {

        @StringFormat
        private String unitId;
    }

}