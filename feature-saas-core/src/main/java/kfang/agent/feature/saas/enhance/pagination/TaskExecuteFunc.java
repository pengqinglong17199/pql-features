package kfang.agent.feature.saas.enhance.pagination;

import java.util.List;

/**
 * 多任务函数
 *
 * @author hyuga
 */
@FunctionalInterface
public interface TaskExecuteFunc<V> {

    /**
     * 分页任务查询后置函数执行
     *
     * @param dataList 数据集
     */
    void execute(List<V> dataList);

}
