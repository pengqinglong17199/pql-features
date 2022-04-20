package kfang.agent.feature.saas.sync;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.object.ObjectUtil;
import kfang.agent.feature.saas.HyugaRejectedExecutionHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SyncTask
 *
 * @author pengqinglong
 * @since 2022/4/20
 */
@Slf4j
public class SyncTask<T> {
    /**
     * 异步任务内部线程池
     */
    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(10), new HyugaRejectedExecutionHandler());


    private final Task<T> task;

    /**
     * 私有构造方法
     */
    private SyncTask(List<T> source){
        this.task = new Task<>(source);
    }

    public static <T> SyncTask<T> data(List<T> source){
        return new SyncTask<>(source);
    }

    public SyncTask<T> addTask(TaskEvent event, SingleTask<T> task){
        this.task.addTask(event, task);
        return this;
    }

    public SyncTask<T> addTask(TaskEvent event, AllTask<T> task, Comparable<T, Result> comparable){
        this.task.addTask(event, task, comparable);
        return this;
    }

    public void exec(TaskConsumer<T> consumer) throws Exception {
        this.task.exec(consumer);
    }

    private static class Task<T>{

        private final BlockingDeque<QueueResult<T>> queue;

        private final List<T> source;

        private final List<Runnable> taskList;

        private final AtomicInteger taskCount;

        private Thread main;

        public Task(List<T> list){
            this.source = list;
            this.queue = new LinkedBlockingDeque<>();
            this.taskList = ListUtil.newArrayList();
            this.taskCount = new AtomicInteger(0);
        }

        public Task<T> addTask(TaskEvent event, AllTask<T> task, Comparable<T, Result> comparable){
            taskList.add(this.createAllTask(event, task, comparable));
            this.taskCount.incrementAndGet();
            return this;
        }

        public Task<T> addTask(TaskEvent event, SingleTask<T> task){
            taskList.add(this.createSingleTask(event, task));
            this.taskCount.incrementAndGet();
            return this;
        }

        public Runnable createAllTask(TaskEvent event, AllTask<T> task, Comparable<T, Result> comparable) {
            return () -> {
                try {
                    List<? extends Result> call = task.call(ObjectUtil.cast(source));
                    for (Result result : call) {
                        for (T t : source) {
                            if (comparable.comparable(t, result)) {
                                queue.offer(new SyncTask.QueueResult<>(event, t, result));
                                break;
                            }
                        }
                    }
                } finally {
                    // 任何情况下 必须执行任务结束
                    this.taskComplete();
                }
            };
        }

        private Runnable createSingleTask(TaskEvent event, SingleTask<T> task){
            return () -> {
                try {
                    source.forEach(t -> {

                        Result result = task.call(ObjectUtil.cast(t));
                        queue.offer(new SyncTask.QueueResult<>(event, t, result));
                    });
                } finally {
                    // 任何情况下 必须执行任务结束
                    this.taskComplete();
                }
            };
        }

        /**
         * 任务完成后的线程回调
         */
        private void taskComplete() {
            int i = this.taskCount.decrementAndGet();

            // decrementAndGet 保证了原子性 无需加锁
            if(i == 0){

                // 打断线程阻塞 保证在异常情况下 main线程未结束
                main.interrupt();
            }
        }

        public void exec(TaskConsumer<T> consumer) throws Exception {

            // 最终调用exec的线程作为main线程
            this.main = Thread.currentThread();

            // 保存任务future
            List<Future<?>> futureList = ListUtil.newArrayList(taskList.size());

            // 提交任务
            taskList.forEach(task -> futureList.add(pool.submit(task)));

            // 处理任务结果
            this.handle(futureList, consumer);

        }

        private void handle(List<Future<?>> futureList, TaskConsumer<T> consumer) throws ExecutionException, InterruptedException {
            try{

                // 阻塞等待任务结果
                while (true){
                    QueueResult<T> queueResult = queue.take();

                    // 消费
                    consumer.consumer(queueResult.event, queueResult.source, queueResult.result);

                    // 单线程计算等待任务执行完成
                    int i = this.taskCount.get();
                    if(i == 0){
                        return;
                    }
                }
            }catch (InterruptedException e){
                log.warn("异常原因-线程打断", e);
            }finally {

                // 任务执行完成后判断是否有异常
                for (Future<?> future : futureList) {
                    future.get();
                }
            }
        }
    }

    @Data
    @AllArgsConstructor
    public static class QueueResult<T>{
        private TaskEvent event;
        private T source;
        private Result result;

    }

    /**
     * 任务结果消费
     * @param <T>
     */
    public interface TaskConsumer<T>{

        void consumer(TaskEvent event, T source, Result result);
    }

    /**
     * 比较函数 用于比较两个对象是否是同一个
     * @param <T>
     * @param <R>
     */
    public interface Comparable<T, R>{

        boolean comparable(T source, Result result);
    }
}






