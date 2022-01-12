package kfang.agent.feature.saas.diff.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.lang.reflect.Field;

/**
 * OperationLogFieldWrapper
 *
 * @author pengqinglong
 * @since 2021/3/24
 */
@Data
public class OperationLogFieldWrapper {

    @ApiModelProperty(value = "父字段的order")
    private int parentOrder;

    @ApiModelProperty(value = "父字段的name")
    private Field parent;

    @ApiModelProperty(value = "字段对象")
    private Field field;

    public OperationLogFieldWrapper() {
    }

    public OperationLogFieldWrapper(Field field) {
        this.field = field;
    }
}