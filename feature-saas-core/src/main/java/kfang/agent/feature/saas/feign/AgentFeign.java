package kfang.agent.feature.saas.feign;

import kfang.agent.feature.saas.feign.enums.ServiceSignEnum;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 盘客feign处理注解
 *
 * @author hyuga
 * @since 2022/01/07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({AgentFeignConfiguration.class, AgentFeignCore.class})
public @interface AgentFeign {

    /**
     * 是否隔离不同环境的service服务 默认开启 测试环境有效 正式环境无效
     */
    boolean isolation();

    /**
     * 指定隔离的服务名标识 请求的service服务名中包含该标识才进行隔离
     */
    ServiceSignEnum serviceSign() default ServiceSignEnum.AGENT;

    /**
     * vpn等场景 测试环境允许跳过192.168.3.*
     */
    boolean skipSpecialIpAddressSegment() default false;

}
