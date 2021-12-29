package kfang.agent.feature.saas.sql.isolation.annotation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.*;

/**
 * 跳过数据隔离注解
 *
 * @author pengqinglong
 * @since 2021/12/15
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SkipDataIsolation {

    /**
     * 需要跳过的数据级别
     */
    Level level() default Level.ALL;

    /**
     * 跳过的原因备注
     */
    String note()default "";

    @AllArgsConstructor
    @Getter
    enum Level {

        /**
         * 全部跳过 方便拓展 全部跳过的序号为0
         */
        ALL("", ""),
        /**
         * 平台级别
         */
        PLATFORM("FPLATFORM_ORG_ID", "platformOrgId"),
        ;

        private final String sqlFieldName;

        private final String javaFieldName;
    }

}
