package kfang.agent.feature.saas.export;

import java.lang.annotation.*;

/**
 * 盘客切面导出限制
 *
 * @author fjk
 * @since 2021-08-11
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AgentExportLimit {

    /**
     * 导出缓存key
     */
    String key();

    int exportCount() default 10;

    int timeSeconds() default 60;

}
