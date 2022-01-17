package kfang.agent.feature.saas.enhance.pagination;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.date.interval.TimeInterval;
import cn.hyugatool.core.instance.ReflectionUtil;
import cn.hyugatool.core.string.snow.SnowflakeIdUtil;
import io.swagger.annotations.ApiModelProperty;
import kfang.agent.feature.saas.logger.LogModule;
import kfang.agent.feature.saas.logger.util.LogUtil;
import kfang.infra.api.validate.SecurityPageForm;
import kfang.infra.api.validate.extend.PageExtendForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 多任务分页查询工具类
 * 注：使用该工具查询的sql一定要有一个唯一性质的排序，不然多次查询可能导致数据重复
 *
 * @author hyuga
 * @since 2021/6/8
 */
@Slf4j
public final class PaginationQueryAllUtil {

    private static final String CURRENT_PAGE = "currentPage";
    private static final String PAGE_SIZE = "pageSize";
    private static final String QUERY_RECORD_COUNT = "queryRecordCount";

    public static <T, V> List<V> speedyExecute(T pageQueryForm, int sizeOfOneTask, PaginationQueryAllFunc<T, V> paginationQueryAllFunc) {
        return speedyExecute(LogModuleEnum.PAGINATION_QUERY_ALL.getDesc(), pageQueryForm, sizeOfOneTask, paginationQueryAllFunc);
    }

    public static <T, V> List<V> speedyExecute(String logModuleEnum, T pageQueryForm, int sizeOfOneTask, PaginationQueryAllFunc<T, V> paginationQueryAllFunc) {
        setPageIndexRang(pageQueryForm, 1, 1, true);
        // 先得到总数据量
        int recordCount = paginationQueryAllFunc.pageQuery(pageQueryForm).getRecordCount();

        if (recordCount <= 0) {
            LogUtil.info(log, logModuleEnum, String.format("源数据总量:%s,任务结束", recordCount));
            return ListUtil.emptyList();
        }

        List<V> result = ListUtil.newArrayList();

        int numberOfTotalPages = (recordCount / sizeOfOneTask) + (((recordCount % sizeOfOneTask) > 0) ? 1 : 0);

        LogUtil.info(log, logModuleEnum, String.format("结果集数据总量:%s,每次查询size:%s,总查询次数:%s", recordCount, sizeOfOneTask, numberOfTotalPages));

        for (int pageNumber = 1; pageNumber < numberOfTotalPages + 1; pageNumber++) {
            setPageIndexRang(pageQueryForm, pageNumber, sizeOfOneTask, false);
            List<V> pageQuery = paginationQueryAllFunc.pageQuery(pageQueryForm).getItems();
            result.addAll(pageQuery);
            LogUtil.info(log, logModuleEnum, String.format("当前查询次数:%s,结果集数量:%s", pageNumber, pageQuery.size()));
        }
        LogUtil.info(log, logModuleEnum, String.format("查询完成,结果集总量:%s,共查询:%s次", result.size(), numberOfTotalPages));
        return result;
    }

    public static <T, V> TaskResult speedyExecute(T pageQueryForm, int sizeOfOneTask, PaginationQueryAllFunc<T, V> paginationQueryAllFunc, TaskExecuteFunc<V> taskExecuteFunc) {
        return speedyExecute(LogModuleEnum.PAGINATION_QUERY_EXECUTE_ALL.getDesc(), pageQueryForm, sizeOfOneTask, paginationQueryAllFunc, taskExecuteFunc);
    }

    public static <T, V> TaskResult speedyExecute(String logModuleEnum, T pageQueryForm, int sizeOfOneTask, PaginationQueryAllFunc<T, V> paginationQueryAllFunc, TaskExecuteFunc<V> taskExecuteFunc) {
        LogUtil.info(log, logModuleEnum, "任务开始");

        TaskResult taskResult = new TaskResult();
        taskResult.setSizeOfOneTask(sizeOfOneTask);
        String snowflakeIdStr = SnowflakeIdUtil.getSnowflakeIdStr();
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.start(snowflakeIdStr);

        setPageIndexRang(pageQueryForm, 1, 1, true);
        // 先得到总数据量
        int recordCount = paginationQueryAllFunc.pageQuery(pageQueryForm).getRecordCount();
        taskResult.setRecordCount(recordCount);

        if (recordCount <= 0) {
            LogUtil.info(log, logModuleEnum, String.format("源数据总量:%s,任务结束", recordCount));
            return taskResult;
        }

        int numberOfTotalPages = (recordCount / sizeOfOneTask) + (((recordCount % sizeOfOneTask) > 0) ? 1 : 0);
        LogUtil.info(log, logModuleEnum, String.format("结果集数据总量:%s,每次查询size:%s,总查询次数:%s", recordCount, sizeOfOneTask, numberOfTotalPages));

        taskResult.setExecuteCount(numberOfTotalPages);

        LinkedHashMap<String, String> timeConsumingsMapOfOneTask = new LinkedHashMap<>();
        for (int pageNumber = 1; pageNumber < numberOfTotalPages + 1; pageNumber++) {
            String snowflakeIdStrOfOneTask = SnowflakeIdUtil.getSnowflakeIdStr();
            TimeInterval timeIntervalOfOneTask = new TimeInterval();
            timeIntervalOfOneTask.start(snowflakeIdStrOfOneTask);

            setPageIndexRang(pageQueryForm, pageNumber, sizeOfOneTask, false);
            List<V> pageQuery = paginationQueryAllFunc.pageQuery(pageQueryForm).getItems();
            List<V> dataListOfNoNull = ListUtil.nullFilter(pageQuery);
            if (ListUtil.hasItem(dataListOfNoNull)) {
                taskExecuteFunc.execute(pageQuery);
            }
            String intervalPrettyOfOneTask = timeIntervalOfOneTask.intervalPretty();
            LogUtil.info(log, logModuleEnum, String.format("当前执行次数:%s,处理数据量:%s,耗时:%s", pageNumber, dataListOfNoNull.size(), intervalPrettyOfOneTask));
            timeConsumingsMapOfOneTask.put(String.format("第%s次", pageNumber), intervalPrettyOfOneTask);
        }
        String intervalPretty = timeInterval.intervalPretty(snowflakeIdStr);
        LogUtil.info(log, logModuleEnum, "任务结束", String.format("共执行%s次,每次size:%s,总耗时:%s", numberOfTotalPages, sizeOfOneTask, intervalPretty));
        taskResult.setTimeConsuming(intervalPretty);
        taskResult.setTimeConsumingsOfOneTask(timeConsumingsMapOfOneTask);
        return taskResult;
    }

    private static void setPageIndexRang(Object pageQueryForm, int currentPage, int pageSize, boolean queryRecordCount) {
        boolean isInfraPagination = pageQueryForm instanceof SecurityPageForm;
        boolean isKfangPagination = pageQueryForm instanceof PageExtendForm;

        if (isInfraPagination || isKfangPagination) {
            ReflectionUtil.setFieldValue(pageQueryForm, CURRENT_PAGE, currentPage);
            ReflectionUtil.setFieldValue(pageQueryForm, PAGE_SIZE, pageSize);
            ReflectionUtil.setFieldValue(pageQueryForm, QUERY_RECORD_COUNT, queryRecordCount);
        }
    }

    @Data
    public static class TaskResult {
        @ApiModelProperty(value = "总结果集数量")
        private int recordCount;
        @ApiModelProperty(value = "执行次数")
        private int executeCount;
        @ApiModelProperty(value = "每次执行")
        private int sizeOfOneTask;
        @ApiModelProperty(value = "任务总耗时")
        private String timeConsuming;
        @ApiModelProperty(value = "单项任务耗时")
        private LinkedHashMap<String, String> timeConsumingsOfOneTask;
    }

    @Getter
    @AllArgsConstructor
    enum LogModuleEnum implements LogModule {
        /**
         *
         */
        PAGINATION_QUERY_ALL("分页查询所有"),
        PAGINATION_QUERY_EXECUTE_ALL("分页查询所有&执行");

        private final String desc;

    }

}
