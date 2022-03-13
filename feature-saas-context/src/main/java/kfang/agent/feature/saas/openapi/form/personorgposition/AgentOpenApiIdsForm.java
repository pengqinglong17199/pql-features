package kfang.agent.feature.saas.openapi.form.personorgposition;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * openApi对外开放form
 *
 * @author pengqinglong
 * @since 2021/8/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AgentOpenApiIdsForm extends AgentOpenApiForm {

    @ApiModelProperty(value = "主键ids", required = true)
    @NotEmpty(message = "IDS不得为空")
    private List<String> ids;

}