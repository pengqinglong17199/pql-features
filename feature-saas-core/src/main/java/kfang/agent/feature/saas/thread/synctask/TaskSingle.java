package kfang.agent.feature.saas.thread.synctask;

/**
 * 单个source任务函数接口
 *
 * @author pengqinglong
 */
@FunctionalInterface
public interface TaskSingle<T> {

    /**
     * 多任务调用
     *
     * @param source 源数据
     * @return 结果集
     */
    Result call(T source);

}