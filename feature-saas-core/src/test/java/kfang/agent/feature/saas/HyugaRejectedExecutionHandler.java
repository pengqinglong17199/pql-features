package kfang.agent.feature.saas;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程任务溢出后自定义拒绝策略
 *
 * @author pengqinglong
 * @since 2022/3/9
 */
@Slf4j
public class HyugaRejectedExecutionHandler implements RejectedExecutionHandler {

    /**
     * 阻塞任务队列
     */
    private final BlockingQueue<Runnable> taskQueue;

    /**
     * 阻塞执行器队列
     */
    private BlockingQueue<Runnable> executorQueue;

    public HyugaRejectedExecutionHandler() {
        taskQueue = new LinkedBlockingDeque<>();
        Thread thread = new Thread(this::handle);
        thread.setDaemon(true);
        thread.start();
    }

    @SuppressWarnings("all")
    public void handle() {
        // 死循环 take阻塞线程 put阻塞线程
        while (true) {
            try {
                Runnable take = taskQueue.take();
                executorQueue.put(take);
            } catch (InterruptedException e) {
                log.error("线程池溢出任务处理异常:{}", e.getMessage(), e);
            }
        }
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        // 初始化executorQueue
        if (executorQueue == null) {
            synchronized (taskQueue) {
                // 不用双重检查 因为就算重新赋值也是一样的
                executorQueue = executor.getQueue();
            }
        }
        // 将溢出的任务放入taskQueue中
        taskQueue.offer(r);
    }

}