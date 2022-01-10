package kfang.agent.feature.saas.feign.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务隔离状态
 *
 * @author hyuga
 * @since 2022-01-07 17:49:20
 */
@Getter
@AllArgsConstructor
public enum ServiceIsolationEnum {

    /**
     *
     */
    DEFAULT("默认"),
    ISOLATION("隔离"),
    ;

    private final String desc;

}
