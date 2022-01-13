package kfang.agent.feature.saas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 环境
 *
 * @author hyuga
 * @since 2022-01-13 19:03:17
 */
@Getter
@AllArgsConstructor
public enum EnvironmentEnum {

    /**
     *
     */
    DEV("测试环境"),
    PRE("预发布环境"),
    PRO("生产环境"),
    ;
    private final String desc;

}
