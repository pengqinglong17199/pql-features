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
    AGENT_UAT_60("60"),
    AGENT_UAT_63("63"),
    AGENT_UAT_64("64"),

    /**
     * 开发个人环境
     */
    AGENT_DEV_PQL("pengqinglong-deiMac.local"),
    ;

    private final String suffix;

    public static void main(String[] args) {
        System.out.println(SystemUtil.getLocalHostName());
    }
}