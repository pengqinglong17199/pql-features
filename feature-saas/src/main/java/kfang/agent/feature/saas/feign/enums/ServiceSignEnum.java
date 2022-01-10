package kfang.agent.feature.saas.feign.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务标识
 *
 * @author hyuga
 * @since 2022-01-07 17:49:20
 */
@Getter
@AllArgsConstructor
public enum ServiceSignEnum {

    /**
     *
     */
    AGENT("-agent-", "盘客"),
    TUOKEBEN("-tuokeben-", "拓客本"),
    ;

    private final String sign;

    private final String desc;

}
