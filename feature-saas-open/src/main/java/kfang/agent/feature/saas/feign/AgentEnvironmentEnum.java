package kfang.agent.feature.saas.feign;

import cn.hyugatool.system.SystemUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ProfileEnum
 *
 * @author pengqinglong
 * @since 2022/4/12
 */
@AllArgsConstructor
@Getter
public enum AgentEnvironmentEnum {

    /**
     * 测试环境
     */
    AGENT_UAT_54(""),
    AGENT_UAT_60("60"),
    AGENT_UAT_63("63"),
    AGENT_UAT_85("85"),

    /**
     * 开发个人环境
     */
    AGENT_DEV_PQL("pengqinglong-deiMac.local"),
    AGENT_DEV_HZY_MAC("MacBook-Pro.local"),
    AGENT_DEV_HZY_COMPANY("hyuga-iMac.local"),
    AGENT_DEV_FJK("fjk"),
    AGENT_DEV_ZY("zy"),
    AGENT_DEV_LW("lw"),
    AGENT_DEV_FW("fw"),
    AGENT_DEV_ZCL("zcl"),
    AGENT_DEV_CH("pkchenhao"),
    ;

    private final String suffix;

    public static void main(String[] args) {
        System.out.println(SystemUtil.getLocalHostName());
    }
}