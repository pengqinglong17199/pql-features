package kfang.agent.feature.saas.openapi.form.person;

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

}