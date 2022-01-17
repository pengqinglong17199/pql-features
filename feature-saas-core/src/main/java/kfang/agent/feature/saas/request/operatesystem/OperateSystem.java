package kfang.agent.feature.saas.request.operatesystem;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 操作系统来源赋值
 *
 * @author hyuga
 * @since 2021-12-21
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
// @Inherited  //可以继承
@Import({OperateSystemAspect.class})
public @interface OperateSystem {

    /**
     * 导出缓存key
     */
    String system();

    boolean penetration() default false;

}
