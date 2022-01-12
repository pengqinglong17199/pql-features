package kfang.agent.feature.saas.hidden;

import kfang.agent.feature.saas.hidden.aspect.HiddenResultAspect;
import kfang.agent.feature.saas.sql.AgentSqlConfiguration;
import kfang.agent.feature.saas.sql.AgentSqlCore;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * AgentHidden
 *
 * @author pengqinglong
 * @since 2022/1/7
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HiddenResultAspect.class)
public @interface AgentHidden {
}