package kfang.agent.feature.dingtalk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 盘客开发枚举
 *
 * @author pengqinglong
 * @since 2022/1/21
 */
@AllArgsConstructor
@Getter
public enum Author {

    /**
     *
     */
    HUANG_ZE_YUAN("黄泽源", "15919907075"),
    PENG_QING_LONG("彭清龙", "13265116714"),
    LIU_WEI("刘威", "18274805040"),
    ZHONG_YUAN("钟源", "13924592193"),
    CHEN_YI("陈毅", "15026677661"),
    CHEN_HAO("陈豪", "13714251547"),
    FAN_WEN("范威", "13066868044"),
    ZHANG_CHENG_LONG("张呈龙", "18682051332"),
    ;

    private final String name;
    private final String phone;

}