package kfang.agent.feature.monitor.model;

import kfang.agent.feature.monitor.enums.ProjectEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * ServiceModel
 *
 * @author pengqinglong
 * @since 2021/12/1
 */
@Data
public class ServiceMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目
     */
    private ProjectEnum project;

    /**
     * IP
     */
    private String ip;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 服务名
     */
    private String name;

    /**
     * 环境
     */
    private String environment;

}