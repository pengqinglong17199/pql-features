package kfang.agent.feature.saas.swagger;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用swagger拓展器
 *
 * @author hyuga
 * @since 2022/1/21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan(value = {"springfox.documentation"})
@Import(SwaggerModelParameterExpander.class)
public @interface EnableSwaggerExpander {
}