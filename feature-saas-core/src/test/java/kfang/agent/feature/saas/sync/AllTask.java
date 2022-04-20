package kfang.agent.feature.saas.sync;

import java.util.List;

/**
 * 多个source任务函数
 */
@FunctionalInterface
public interface AllTask<T> {

    List<? extends Result> call(List<T> source);
}