package kfang.agent.feature.saas.sync;

/**
 * 单个source任务函数
 */
@FunctionalInterface
public interface TaskSingle<T> {

    Result call(T source);

}