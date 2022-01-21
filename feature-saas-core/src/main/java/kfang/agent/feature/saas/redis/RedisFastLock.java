package kfang.agent.feature.saas.redis;

import kfang.agent.feature.saas.redis.func.RedisLockTaskInterface;
import kfang.infra.common.cache.KfangCache;
import kfang.infra.common.cache.lock.KfangLock;
import lombok.extern.slf4j.Slf4j;

/**
 * RedisFastLock
 * Redis分布式锁快速使用
 *
 * @author hyuga
 * @since 2020-11-12 下午2:59
 */
@Slf4j
public final class RedisFastLock {

    private static final int DEFAULT_TRY_TIME = 3;

    /**
     * 分布式锁模板方法
     *
     * @param lockKey    分布式锁key
     * @param agentCache redis对象
     * @param task       任务
     * @param <T>        T
     * @return T
     */
    public static <T> T fastLock(String lockKey, KfangCache agentCache, RedisLockTaskInterface<T> task) {
        return fastLock(lockKey, agentCache, task, null);
    }

    /**
     * 分布式锁模板方法
     *
     * @param lockKey    分布式锁key
     * @param agentCache redis对象
     * @param task       任务
     * @param e          自定义异常
     * @param <T>        T
     * @return T
     */
    public static <T> T fastLock(String lockKey, KfangCache agentCache, RedisLockTaskInterface<T> task, RuntimeException e) {
        return fastLock(lockKey, agentCache, task, null, e);
    }

    /**
     * 分布式锁模板方法
     *
     * @param lockKey    分布式锁key
     * @param agentCache redis对象
     * @param task       任务
     * @param <T>        T
     * @param tryTime    重试次数
     * @return T
     */
    public static <T> T fastLock(String lockKey, KfangCache agentCache, RedisLockTaskInterface<T> task, int tryTime) {
        return fastLock(lockKey, agentCache, task, tryTime, null);
    }

    /**
     * 分布式锁模板方法
     *
     * @param lockKey    分布式锁key
     * @param agentCache redis对象
     * @param task       任务
     * @param tryTime    重试次数
     * @param e          自定义异常
     * @param <T>        T
     * @return T
     */
    public static <T> T fastLock(String lockKey, KfangCache agentCache, RedisLockTaskInterface<T> task, Integer tryTime, RuntimeException e) {
        //分布式锁
        KfangLock kfangLock = new KfangLock(agentCache, lockKey);
        try {
            if (tryTime == null) {
                tryTime = DEFAULT_TRY_TIME;
            }
            if (kfangLock.tryLock(tryTime)) {
                return task.redisLock();
            } else {
                log.info("分布式锁获取失败，重试次数：{}，key ==> {}", tryTime, lockKey);
                //系统繁忙，请稍后重试
                if (e != null) {
                    throw e;
                } else {
                    throw new RuntimeException("系统繁忙，请稍后重试.");
                }
            }
        } finally {
            kfangLock.unLock();
        }
    }

}
