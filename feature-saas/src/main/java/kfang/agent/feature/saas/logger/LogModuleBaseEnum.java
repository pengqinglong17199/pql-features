package kfang.agent.feature.saas.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * LogModuleBaseEnum
 *
 * @author pengqinglong
 * @since 2022/1/10
 */
@AllArgsConstructor
public enum LogModuleBaseEnum implements LogModule {

    /**
     * Other
     */
    SERVICE_WARNING("服务预警"),
    REDIS_CACHE("Redis缓存"),
    PAGINATION_QUERY_ALL("分页查询所有"),
    PAGINATION_QUERY_EXECUTE_ALL("分页查询所有&执行"),
    OPERATOR_INFO("操作人信息"),
    REQUEST_PARAM_FORMAT("请求参数格式化"),
    SPECIAL_REQUEST("特殊请求"),

    /**
     * XXL-JOB
     */
    XXL_JOB("定时任务");

    @Getter
    private final String desc;

}