package kfang.agent.feature.saas.openapi.form;

import io.swagger.annotations.ApiModelProperty;
import kfang.agent.feature.saas.openapi.form.person.AgentOpenApiForm;
import kfang.agent.feature.saas.utils.MapperUtil;
import kfang.infra.api.ValidationForm;
import kfang.infra.api.validate.extend.utils.ReflectionUtil;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * saas对外apiForm
 *
 * @author hyuga
 * @since 2022-03-13 16:31:27
 */
@Data
public class AgentOpenApiBaseForm implements ValidationForm {

    @ApiModelProperty(value = "平台组织ID", required = true)
    @NotBlank(message = "平台组织ID不能为空")
    private String platformOrgId;

    @ApiModelProperty(value = "操作人ip", required = true)
    @NotBlank(message = "操作人ip不能为空")
    private String operatorIp;

    @ApiModelProperty(value = "操作系统来源", hidden = true)
    private String operatorSystem;

    /**
     * form对象拷贝
     *
     * @param tClass 原对象class
     * @return 目标对象
     */
    public <T, R> R derive(Class<T> tClass) {
        try {
            T target = ReflectionUtil.newInstance(tClass);
            setBasicParamsOfOperator(target);
            return ReflectionUtil.cast(target);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * form对象拷贝
     *
     * @param tClass 原对象class
     * @return 目标对象
     */
    public <T, R> R deriveAll(Class<T> tClass) {
        try {
            T target = ReflectionUtil.newInstance(tClass);
            MapperUtil.copy(this, target);
            return ReflectionUtil.cast(target);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 设置操作人的基本参数
     *
     * @param target 目标对象
     * @param <T>    T
     */
    private <T> void setBasicParamsOfOperator(T target) {
        List<String> declaredFieldNames = ReflectionUtil.getDeclaredFieldNames(AgentOpenApiForm.class);
        declaredFieldNames.forEach(fieldName -> {
            Object fieldValue = ReflectionUtil.getFieldValue(this, fieldName);
            ReflectionUtil.setFieldValue(target, fieldName, fieldValue);
        });
    }

}
