package kfang.agent.feature.saas.enhance.pagination;

import java.util.List;

/**
 * 多任务函数
 *
 * @author hyuga
 */
@FunctionalInterface
public interface TaskExecuteFunc<V> {

    void execute(List<V> datas);

}
