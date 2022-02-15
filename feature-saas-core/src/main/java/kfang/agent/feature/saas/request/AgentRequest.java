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
@Import({BlockRepeatedRequests.class, RequestParamFormatAspect.class, AgentRequestConfiguration.class})
public @interface AgentRequest {

    /**
     * 请求参数是否填充基础信息
     */
    boolean basicParam() default false;

    /**
     * 请求次数限制
     */
    boolean requestLimit() default false;
}
