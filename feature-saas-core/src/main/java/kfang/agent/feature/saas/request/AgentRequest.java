package kfang.agent.feature.saas.request;

import kfang.agent.feature.saas.request.commit.BlockRepeatedRequests;
import kfang.agent.feature.saas.request.format.core.RequestParamFormatAspect;
import kfang.agent.feature.saas.sql.AgentSqlConfiguration;
import kfang.agent.feature.saas.sql.AgentSqlCore;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 盘客请求参数处理
 *
 * @author pengqinglong
 * @since 2022-01-10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({BlockRepeatedRequests.class, RequestParamFormatAspect.class})
public @interface AgentRequest {
}
