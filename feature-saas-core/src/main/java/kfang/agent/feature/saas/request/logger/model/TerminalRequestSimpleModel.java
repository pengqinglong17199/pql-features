package kfang.agent.feature.saas.request.logger.model;

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
public class TerminalRequestSimpleModel {

    @ApiModelProperty(value = "请求ip")
    private String requestIp;
    @ApiModelProperty(value = "请求时间")
    private String requestTime;
    @ApiModelProperty(value = "请求方式")
    private String requestMethod;
    @ApiModelProperty(value = "请求url")
    private String requestUrl;
    @ApiModelProperty(value = "服务器ip-内网")
    private String serverIp;
    @ApiModelProperty(value = "响应状态码")
    private String statusCode;
    @ApiModelProperty(value = "请求持续时间")
    private String costTime;
    @ApiModelProperty(value = "请求参数")
    private String requestParams;
    @ApiModelProperty(value = "请求Body")
    private String requestBody;

}
