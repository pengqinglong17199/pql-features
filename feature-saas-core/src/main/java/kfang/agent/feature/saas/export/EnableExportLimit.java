package kfang.agent.feature.saas.export;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 打开导出限制
 *
 * @author pql
 * @since 2022/3/9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(AgentExportLimitAspect.class)
public @interface EnableExportLimit {
}
