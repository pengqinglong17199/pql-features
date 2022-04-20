package kfang.agent.feature.saas.sync;

import java.util.concurrent.BlockingQueue;

/**
 * 单个source任务函数
 */
@FunctionalInterface
public interface SingleTask<T> {

    Result call(T source);

}