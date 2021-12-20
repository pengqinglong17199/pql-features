package kfang.agent.feature.monitor.model;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "端口")
    private Integer port;

    @ApiModelProperty(value = "服务名")
    private String name;

    @ApiModelProperty(value = "环境")
    private String environment;

}