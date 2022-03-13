package kfang.agent.feature.saas.openapi.form.person;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * openApi对外开放form
 *
 * @author pengqinglong
 * @since 2021/8/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AgentOpenApiIdForm extends AgentOpenApiForm {

    @ApiModelProperty(value = "主键id", required = true)
    @NotEmpty(message = "ID不得为空")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AgentOpenApiIdForm() {
    }

    public AgentOpenApiIdForm(String id) {
        this.id = id;
    }

}