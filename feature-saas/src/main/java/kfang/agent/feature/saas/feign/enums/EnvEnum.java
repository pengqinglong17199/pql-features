package kfang.agent.feature.saas.feign.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统环境
 *
 * @author hyuga
 * @since 2022-01-10 09:14:35
 */
@AllArgsConstructor
@Getter
public enum EnvEnum {

    /**
     *
     */
    DEV_ENV("开发环境"),
    TEST_ENV("测试环境"),
    ;

    private final String desc;
}
