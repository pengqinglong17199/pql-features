package kfang.agent.feature.monitor.enums;

import kfang.agent.feature.lombok.annotations.EnumDesc;

/**
 * Demo2
 *
 * @author pengqinglong
 * @since 2022/5/9
 */
public class Demo2 {

    @EnumDesc(filed = "projName")
    private ProjectEnum project1;

    @EnumDesc
    @EnumDesc(filed = "projName")
    private ProjectEnum project2;
}