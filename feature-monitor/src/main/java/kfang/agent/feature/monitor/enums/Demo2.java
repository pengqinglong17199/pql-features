package kfang.agent.feature.monitor.enums;

import kfang.agent.feature.lombok.pql.annotations.EnumDesc;

/**
 * Demo2
 *
 * @author pengqinglong
 * @since 2022/5/9
 */
@EnumDesc
public class Demo2 {

    @EnumDesc(filed = {"projName", "desc"})
    private ProjectEnum project1;

    @EnumDesc
    private ProjectEnum project2;

    private ProjectEnum project3;

}