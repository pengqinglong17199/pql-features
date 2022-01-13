package kfang.agent.feature.monitor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 项目枚举
 *
 * @author hyuga
 * @since 2022-01-12 15:08:31
 */
@Getter
@AllArgsConstructor
public enum ProjectEnum {

    /**
     *
     */
    AGENT("房客宝"),
    TUOKEBEN("拓客本"),
    ;

    private final String desc;

}