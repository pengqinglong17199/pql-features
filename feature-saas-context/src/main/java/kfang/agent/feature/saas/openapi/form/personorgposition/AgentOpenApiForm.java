package kfang.agent.feature.saas.openapi.form.personorgposition;

import io.swagger.annotations.ApiModelProperty;
import kfang.agent.feature.saas.openapi.form.AgentOpenApiBaseForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

/**
 * openApi对外开放form
 *
 * @author pengqinglong
 * @since 2021/8/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AgentOpenApiForm extends AgentOpenApiBaseForm {

    @ApiModelProperty(value = "操作人ID", required = true)
    @NotBlank(message = "操作人ID不能为空")
    private String operatorId;

    @ApiModelProperty(value = "操作人姓名", required = true)
    @NotBlank(message = "操作人姓名不能为空")
    private String operatorName;

    @ApiModelProperty(value = "操作人组织ID", required = true)
    @NotBlank(message = "操作人组织ID不能为空")
    private String operatorOrgId;

    @ApiModelProperty(value = "操作人组织名称", required = true)
    @NotBlank(message = "操作人组织名称不能为空")
    private String operatorOrgName;

    @ApiModelProperty(value = "操作人岗位ID", required = true)
    @NotBlank(message = "操作人岗位ID不能为空")
    private String operatorPositionId;

    @ApiModelProperty(value = "操作人岗位名称", required = true)
    @NotBlank(message = "操作人岗位名称不能为空")
    private String operatorPositionName;

}