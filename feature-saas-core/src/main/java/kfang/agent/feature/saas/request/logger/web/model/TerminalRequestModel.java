package kfang.agent.feature.saas.request.logger.web.model;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 终端请求日志
 *
 * @author hyuga
 * @since 2021/3/11
 */
@Getter
@Setter
public class TerminalRequestModel {

    @ApiModelProperty(value = "请求ip")
    private String requestIp;
    @ApiModelProperty(value = "请求时间")
    private String requestTime;
    @ApiModelProperty(value = "请求方式")
    private String requestMethod;
    @ApiModelProperty(value = "请求url")
    private String requestUrl;
    @ApiModelProperty(value = "请求域名")
    @JSONField(serialize = false)
    private String serverDomain;
    @ApiModelProperty(value = "用户代理")
    @JSONField(serialize = false)
    private String userAgent;
    @ApiModelProperty(value = "服务器ip-内网")
    private String serverIp;
    @ApiModelProperty(value = "服务器端口")
    @JSONField(serialize = false)
    private String serverPort;
    @ApiModelProperty(value = "编码方式")
    @JSONField(serialize = false)
    private String encodingType;
    @ApiModelProperty(value = "响应状态码")
    private String statusCode;
    @ApiModelProperty(value = "响应内容大小")
    @JSONField(serialize = false)
    private String responseContextSize;
    @ApiModelProperty(value = "响应时间")
    @JSONField(serialize = false)
    private String responseTime;
    @ApiModelProperty(value = "请求持续时间")
    private String costTime;
    @ApiModelProperty(value = "请求参数")
    private String requestParams;
    @ApiModelProperty(value = "请求Body")
    private String requestBody;
    @ApiModelProperty(value = "响应Body")
    @JSONField(serialize = false)
    private String responseBody;

}
