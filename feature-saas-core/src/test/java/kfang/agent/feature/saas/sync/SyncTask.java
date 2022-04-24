package kfang.agent.feature.saas.sync;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.object.ObjectUtil;
import io.swagger.annotations.ApiModelProperty;
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

    /**
     * 服务停止时关闭线程池
     */
    public static void shutdown(){
        pool.shutdown();
    }


    private final Task<T> task;

    /**
     * 私有构造方法
     */
    private SyncTask(List<T> source){
        this.task = new Task<>(source);
    }

    /**
     * 初始化数据源
     * @return 对Task内部类进行包装 禁止外部访问
     */
    public static <T> SyncTask<T> data(List<T> source){
        return new SyncTask<>(source);
    }

    /**
     * 添加单任务
     * @param event 事件
     * @param task  任务
     */
    public SyncTask<T> addTask(TaskEvent event, SingleTask<T> task){
        this.task.addTask(event, task);
        return this;
    }

    /**
     * 添加多任务
     * @param event 事件
     * @param task 任务
     * @param comparable 对比器 用于对比任务获取到的result与source
     */
    public SyncTask<T> addTask(TaskEvent event, AllTask<T> task, Comparable<T, Result> comparable){
        this.task.addTask(event, task, comparable);
        return this;
    }

    public void exec(TaskConsumer<T> consumer) throws Exception {
        this.task.exec(consumer);
    }

    /**
     * 内部类 整个任务链的封装
     * @param <T>
     */
    private static class Task<T>{

        /**
         * 任务结果队列
         */
        private final BlockingDeque<QueueResult<T>> queue;

        /**
         * 源数据list
         */
        private final List<T> source;

        /**
         * 任务栈
         */
        private Node stack;

        /**
         * 任务栈的数量
         */
        private final AtomicInteger size;

        /**
         * 最终执行的主线程
         */
        private Thread main;

        public Task(List<T> list){
            this.source = list;
            this.queue = new LinkedBlockingDeque<>();
            this.size = new AtomicInteger(0);
        }

        /**
         * 添加all任务
         */
        public Task<T> addTask(TaskEvent event, AllTask<T> task, Comparable<T, Result> comparable){
            this.pushTask(this.createAllTask(event, task, comparable));
            return this;
        }

        /**
         * 添加single任务
         */
        public Task<T> addTask(TaskEvent event, SingleTask<T> task){
            this.pushTask(this.createSingleTask(event, task));
            return this;
        }

        /**
         * 压栈
         */
        private void pushTask(Runnable runnable) {
            stack = new Node(runnable, stack);
            this.size.incrementAndGet();
        }

        /**
         * 创建all任务
         */
        public Runnable createAllTask(TaskEvent event, AllTask<T> task, Comparable<T, Result> comparable) {
            return () -> {
                try {
                    List<? extends Result> call = task.call(ObjectUtil.cast(source));
                    for (Result result : call) {
                        for (T t : source) {
                            if (comparable.comparable(t, result)) {
                                queue.offer(new QueueResult<>(event, t, result));
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

        /**
         * 创建单个任务
         */
        private Runnable createSingleTask(TaskEvent event, SingleTask<T> task){
            return () -> {
                try {
                    source.forEach(t -> {

                        Result result = task.call(ObjectUtil.cast(t));
                        queue.offer(new QueueResult<>(event, t, result));
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
            int i = this.size.decrementAndGet();

            // decrementAndGet 保证了原子性 无需加锁
            if(i == 0){

                // 打断线程阻塞 保证在异常情况下 main线程未结束
                main.interrupt();
            }
        }

        /**
         * 任务结果处理
         * @param consumer  结果消费处理
         */
        public void exec(TaskConsumer<T> consumer) throws Exception {

            // 最终调用exec的线程作为main线程
            this.main = Thread.currentThread();

            // 执行加入的任务
            List<Future<?>> futureList = this.execTask();

            // 处理任务结果
            this.handleResult(futureList, consumer);
        }

        /**
         * 无结果回调处理 将结果处理直接写在任务中的方式
         * （不建议使用 更建议所有处理结果汇总 方便他人接手进行问题排查）
         */
        public void exec() throws Exception {

            // 最终调用exec的线程作为main线程
            this.main = Thread.currentThread();

            // 执行任务
            List<Future<?>> futureList = this.execTask();

            // 任务执行完成后判断是否有异常
            for (Future<?> future : futureList) {
                future.get();
            }
        }

        /**
         * 执行任务
         */
        private List<Future<?>> execTask() {
            // 保存任务future
            List<Future<?>> futureList = ListUtil.newArrayList(size.get());

            // 栈顶任务由主线程执行
            Node main = stack;

            // 提交任务
            Node task;
            while ((task = stack.getNext()) != null){
                futureList.add(pool.submit(task.getTask()));
            }

            // 运行main任务 （run中不处理异常 当main任务异常时直接抛出）
            this.runMain(futureList, main);

            return futureList;
        }

        /**
         * 执行main任务
         */
        private void runMain(List<Future<?>> futureList, Node main) {
            try {
                main.getTask().run();
            }catch (Exception e){
                // 异常 任务全部中断掉
                futureList.forEach(future -> future.cancel(true));
            }
        }

        /**
         * 结果处理
         */
        private void handleResult(List<Future<?>> futureList, TaskConsumer<T> consumer) throws ExecutionException, InterruptedException {
            try{

                // 阻塞等待任务结果
                while (true){
                    QueueResult<T> queueResult = queue.take();

                    // 消费
                    consumer.consumer(queueResult.event, queueResult.source, queueResult.result);

                    // 单线程计算等待任务执行完成
                    int i = this.size.get();
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

        /**
         * 栈节点
         */
        @AllArgsConstructor
        @Data
        private class Node{

            @ApiModelProperty("持有任务")
            private Runnable task;

            @ApiModelProperty(value = "下一位指针")
            private Node next;
        }

        /**
         * 队列结果
         * @param <T>
         */
        @Data
        @AllArgsConstructor
        public static class QueueResult<T>{
            private TaskEvent event;
            private T source;
            private Result result;

        }
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






