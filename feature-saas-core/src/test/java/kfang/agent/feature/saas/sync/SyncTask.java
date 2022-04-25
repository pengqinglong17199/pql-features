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
import java.util.concurrent.atomic.AtomicLong;

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
    private static final ThreadPoolExecutor SYNC_TASK_POOL;

    /**
     * 完成任务总数
     */
    private static AtomicLong COMPLETED_TASKS;

    /**
     * 当前正在并发的任务数
     */
    private static AtomicInteger CURRENT;

    /**
     * 峰值并发
     */
    private static AtomicInteger MAX;

    static {
        HyugaRejectedExecutionHandler handler = new HyugaRejectedExecutionHandler();
        LinkedBlockingDeque<Runnable> workQueue = new LinkedBlockingDeque<>(10);
        SYNC_TASK_POOL = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, workQueue, handler);

        COMPLETED_TASKS = new AtomicLong();
        MAX = new AtomicInteger();
    }

    /**
     * 服务停止时关闭线程池
     */
    public static void shutdown() {
        SYNC_TASK_POOL.shutdown();
    }


    private final Task<T> task;

    /**
     * 私有构造方法
     */
    private SyncTask(List<T> sources) {
        this.task = new Task<>(sources);
    }

    /**
     * 初始化数据源
     *
     * @return 对Task内部类进行包装 禁止外部访问
     */
    public static <T> SyncTask<T> data(List<T> sources) {
        return new SyncTask<>(sources);
    }

    /**
     * 添加单任务
     *
     * @param event 事件
     * @param task  任务
     */
    public SyncTask<T> addTask(TaskEvent event, TaskSingle<T> task) {
        this.task.addTask(event, task);
        return this;
    }

    /**
     * 添加多任务
     *
     * @param event      事件
     * @param task       任务
     * @param comparable 对比器 用于对比任务获取到的result与source
     */
    public SyncTask<T> addTask(TaskEvent event, TaskMultiple<T> task, Comparable<T> comparable) {
        this.task.addTask(event, task, comparable);
        return this;
    }

    public void exec(TaskConsumer<T> consumer) throws Exception {
        this.task.exec(consumer);
    }

    /**
     * 内部类 整个任务链的封装
     *
     * @param <T>
     */
    private static class Task<T> {

        /**
         * 任务结果队列
         */
        private final BlockingDeque<QueueResult<T>> queue;

        /**
         * 源数据list
         */
        private final List<T> sources;

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

        public Task(List<T> list) {
            this.sources = list;
            this.queue = new LinkedBlockingDeque<>();
            this.size = new AtomicInteger(0);
        }

        /**
         * 添加all任务
         */
        public void addTask(TaskEvent event, TaskMultiple<T> task, Comparable<T> comparable) {
            this.pushTask(this.createTaskMultiple(event, task, comparable));
        }

        /**
         * 添加single任务
         */
        public void addTask(TaskEvent event, TaskSingle<T> task) {
            this.pushTask(this.createTaskSingle(event, task));
        }

        /**
         * 压栈
         */
        private void pushTask(Runnable runnable) {
            stack = new Node(runnable, stack);
            this.size.incrementAndGet();
        }

        /**
         * 创建多任务Runnable
         */
        public Runnable createTaskMultiple(TaskEvent event, TaskMultiple<T> task, Comparable<T> comparable) {
            return () -> {
                try {
                    List<? extends Result> results = task.call(ObjectUtil.cast(sources));
                    for (Result result : results) {
                        for (T source : sources) {
                            if (comparable.comparable(source, result)) {
                                queue.offer(new QueueResult<>(event, source, result));
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
        private Runnable createTaskSingle(TaskEvent event, TaskSingle<T> task) {
            return () -> {
                try {
                    CURRENT.incrementAndGet();
                    sources.forEach(source -> {
                        Result result = task.call(ObjectUtil.cast(source));
                        queue.offer(new QueueResult<>(event, source, result));
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

            COMPLETED_TASKS.incrementAndGet();
            int current = CURRENT.getAndDecrement();

            for (int max = MAX.get(); max < current && !MAX.compareAndSet(max, current); ){ }


            int i = this.size.decrementAndGet();
            // decrementAndGet 保证了原子性 无需加锁
            if (i == 0) {
                // 打断线程阻塞 保证在异常情况下 main线程未结束
                main.interrupt();
            }
        }

        /**
         * 任务结果处理
         *
         * @param consumer 结果消费处理
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
            Node mainNode = stack;

            // 提交任务 弹栈帮助gc
            while ((stack = stack.getNext()) != null) {
                // 任务提交线程池
                futureList.add(SYNC_TASK_POOL.submit(stack.getTask()));
            }

            // 运行mainNode任务 （run中不处理异常 当main任务异常时直接抛出）
            this.runMainNode(mainNode, futureList);

            return futureList;
        }

        /**
         * 执行mainNode任务
         * 主栈任务异常 则取消所有子栈任务
         */
        private void runMainNode(Node mainNode, List<Future<?>> futureList) {
            try {
                mainNode.getTask().run();
            } catch (Exception e) {
                // 异常 任务全部中断掉
                futureList.forEach(future -> future.cancel(true));
            }
        }

        /**
         * 结果处理
         * 主线程阻塞等待当前批次线程池内任务消费处理完成
         * 任务执行完成后判断是否有异常
         */
        private void handleResult(List<Future<?>> futureList, TaskConsumer<T> consumer) throws ExecutionException, InterruptedException {
            try {

                // 阻塞等待任务结果
                while (true) {
                    QueueResult<T> queueResult = queue.take();

                    // 消费
                    consumer.consumer(queueResult.event, queueResult.source, queueResult.result);

                    // 单线程计算等待任务执行完成
                    int i = this.size.get();
                    if (i == 0) {
                        return;
                    }
                }
            } catch (InterruptedException e) {
                log.warn("异常原因-线程打断", e);
            } finally {

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
        private static class Node {

            @ApiModelProperty("持有任务")
            private Runnable task;

            @ApiModelProperty(value = "下一位指针")
            private Node next;
        }

        /**
         * 队列结果
         *
         * @param <T>
         */
        @Data
        @AllArgsConstructor
        public static class QueueResult<T> {
            private TaskEvent event;
            private T source;
            private Result result;

        }
    }

    /**
     * 任务结果消费
     *
     * @param <T>
     */
    public interface TaskConsumer<T> {

        void consumer(TaskEvent event, T source, Result result);
    }

    /**
     * 比较函数 用于比较两个对象是否是同一个
     *
     * @param <T>
     */
    public interface Comparable<T> {

        boolean comparable(T source, Result result);
    }

}






