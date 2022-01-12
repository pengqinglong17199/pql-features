package kfang.agent.feature.saas.diff.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * OperationLogBase
 *
 * @author pengqinglong
 * @since 2021/3/24
 */
@Data
public class OperationLogBase {

    @ApiModelProperty(value = "主键id")
    private String id;
}