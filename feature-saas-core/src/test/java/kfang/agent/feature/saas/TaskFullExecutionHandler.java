package kfang.agent.feature.saas;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * TaskFullExecutionHandler
 *
 * @author pengqinglong
 * @since 2022/3/9
 */
@Slf4j
public class TaskFullExecutionHandler implements RejectedExecutionHandler {


    private final BlockingQueue<Runnable> taskQueue;

    private BlockingQueue<Runnable> executorQueue;

    public TaskFullExecutionHandler(){
        taskQueue = new LinkedBlockingDeque<>();
        Thread thread = new Thread(this::handle);
        thread.setDaemon(true);
        thread.start();
    }


    public void handle() {
        // 死循环 take阻塞线程 put阻塞线程
        while (true){
            try {
                Runnable take = taskQueue.take();
                executorQueue.put(take);
            }catch (Exception e){
                log.error("线程池溢出任务处理异常", e);
            }
        }
    }
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        // 初始化executorQueue
        if(executorQueue == null) {
            synchronized (taskQueue) {
                // 不用双重检查 因为就算重新赋值也是一样的
                executorQueue = executor.getQueue();
            }
        }

        // 将溢出的任务放入taskQueue中
        taskQueue.offer(r);
    }
}