package kfang.infra.feature.monitor.model;

import io.swagger.annotations.ApiModelProperty;
import kfang.infra.feature.monitor.enums.StatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ServiceModel
 *
 * @author pengqinglong
 * @since 2021/12/1
 */
@Data
@NoArgsConstructor
public class ServiceLogModel implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "端口")
    private Integer port;

    @ApiModelProperty(value = "服务名")
    private String name;

    @ApiModelProperty(value = "环境")
    private String environment;

    @ApiModelProperty(value = "服务状态")
    private StatusEnum status;

    @ApiModelProperty(value = "服务启动时间")
    private Date serverStartTime;

    @ApiModelProperty(value = "服务最后在线时间")
    private Date lastOnlineTime;

    @ApiModelProperty(value = "离线时长")
    private String offlineDuration;

    @ApiModelProperty(value = "在线时长")
    private String onlineDuration;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "IPS")
    private List<String> ips;

}