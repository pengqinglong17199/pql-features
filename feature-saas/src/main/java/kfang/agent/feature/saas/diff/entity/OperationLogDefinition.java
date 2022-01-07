package kfang.agent.feature.saas.diff.entity;

import io.swagger.annotations.ApiModelProperty;
import kfang.agent.feature.saas.diff.enums.FieldTypeEnum;
import kfang.agent.feature.saas.diff.enums.OtherFiledLocationEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.List;

/**
 * OperationLogDefinition
 *
 * @author pengqinglong
 * @since 2021/3/22
 */
@Data
@NoArgsConstructor
public class OperationLogDefinition {

    @ApiModelProperty(value = "需要格式化的字符串")
    private List<String> stringFormatList;

    @ApiModelProperty(value = "字段类型")
    private FieldTypeEnum fieldType;

    @ApiModelProperty(value = "模块名")
    private String moduleName;

    @ApiModelProperty(value = "是否要拼接模块名")
    private boolean isJoinModuleName;

    @ApiModelProperty(value = "字段名")
    private String fieldName;

    @ApiModelProperty(value = "是否跳过")
    private boolean skip;

    @ApiModelProperty(value = "是否要拼接字段名")
    private boolean isJoinFieldName;

    @ApiModelProperty(value = "父字段下标")
    private int parentOrder;

    @ApiModelProperty(value = "排序下标")
    private int order;

    @ApiModelProperty(value = "前缀")
    private String prefix;

    @ApiModelProperty(value = "分隔符")
    private String separator;

    @ApiModelProperty(value = "后缀")
    private String suffix;

    @ApiModelProperty(value = "是否敏感加密")
    private boolean isSensitive;

    @ApiModelProperty(value = "头部保留位数")
    private int headRetain;

    @ApiModelProperty(value = "尾部保留位数")
    private int tailRetain;

    @ApiModelProperty(value = "是否需要合并")
    private boolean isMerge;

    @ApiModelProperty(value = "合并后的字段名")
    private String mergeName;

    @ApiModelProperty(value = "需要合并的字段总数")
    private int mergeSum;

    @ApiModelProperty(value = "合并字段排序下标")
    private int mergeIndex;

    @ApiModelProperty(value = "合并时字段值的拼接前缀")
    private String mergePrefix;

    @ApiModelProperty(value = "合并时字段值的拼接后缀")
    private String mergeSuffix;

    @ApiModelProperty(value = "模块boss字段")
    private boolean leader;

    @ApiModelProperty(value = "是否要拼接其他字段")
    private boolean isJoinOtherFiled;

    @ApiModelProperty(value = "连接其他字段前缀")
    private String otherFiledPrefix;

    @ApiModelProperty(value = "其他字段拼接位子")
    private OtherFiledLocationEnum otherFiledLocation;

    @ApiModelProperty(value = "连接其他字段")
    private String otherFiledName;

    @ApiModelProperty(value = "连接其他字段后缀")
    private String otherFiledSuffix;

    @ApiModelProperty(value = "字段")
    private Field field;


    /**
     * 获取排序 优先返回父字段排序 父字段为0时返回自身的order
     */
    public int getDefinitionOrder() {
        if (parentOrder > 0) {
            return parentOrder;
        }
        return order;
    }

    /**
     * 获取字段真实名字
     */
    public String getFieldRealName() {
        return field == null ? "" : field.getName();
    }
}