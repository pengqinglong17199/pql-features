package kfang.agent.feature.saas.thread.synctask;

/**
 * 异常处理
 *
 * @author pengqinglong
 * @since 2022/4/28
 */
@FunctionalInterface
public interface ErrorHandle {

    <T> void  handle(Exception e, TaskEvent event, T source);
}