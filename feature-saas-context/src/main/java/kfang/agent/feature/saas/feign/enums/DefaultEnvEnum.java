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
public enum DefaultEnvEnum {

    /**
     *
     */
    ENV_54("54", "54测试环境"),
    ENV_60("60", "60测试环境"),
    ENV_63("63", "63测试环境"),
    ENV_85("85", "85测试环境"),
    ;

    private final String ipSuffix;

    private final String desc;

}
