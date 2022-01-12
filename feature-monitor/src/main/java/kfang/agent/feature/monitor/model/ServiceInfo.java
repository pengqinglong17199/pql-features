package kfang.agent.feature.monitor.model;

import cn.hyugatool.core.collection.ListUtil;
import io.swagger.annotations.ApiModelProperty;
import kfang.agent.feature.monitor.enums.ProjectEnum;
import kfang.agent.feature.monitor.enums.StatusEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 服务信息
 *
 * @author hyuga
 * @since 2021-11-26 11:33:22
 */
public class ServiceInfo {

    @Setter
    @Getter
    @ApiModelProperty(value = "项目")
    private ProjectEnum project;
    @Setter
    @Getter
    @ApiModelProperty(value = "服务名")
    private String name;
    @Setter
    @Getter
    @ApiModelProperty(value = "服务端口")
    private int port;
    @Getter
    @ApiModelProperty(value = "服务对应多服务器集合")
    private final List<ServiceIpInfo> ipServiceInfoList = ListUtil.newArrayList();

    @Data
    public static class ServiceIpInfo {
        @ApiModelProperty(value = "IP")
        private String ip;
        @ApiModelProperty(value = "端口")
        private int port;
        @ApiModelProperty(value = "IP后缀")
        private int ipTail;
        @ApiModelProperty(value = "服务状态")
        private StatusEnum status;
        @ApiModelProperty(value = "服务开始时间")
        private Date serverStartTime;
        @ApiModelProperty(value = "服务最后在线时间")
        private Date lastOnlineTime;
        @ApiModelProperty(value = "在线时长")
        private String onlineDuration;
        @ApiModelProperty(value = "离线时长")
        private String offlineDuration;

        public ServiceIpInfo(ServiceLogModel model) {
            this.ip = model.getIp();
            this.port = model.getPort();
            this.ipTail = Integer.parseInt(ip.substring(ip.lastIndexOf(".") + 1));
            this.status = model.getStatus();
            this.serverStartTime = model.getServerStartTime();
            this.lastOnlineTime = model.getLastOnlineTime();
            this.offlineDuration = model.getOfflineDuration();
            this.onlineDuration = model.getOnlineDuration();
        }
    }

}
