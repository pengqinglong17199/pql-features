package kfang.agent.feature.saas.redis.func;

/**
 * RedisLockTaskInterface
 *
 * @author hyuga
 * @since 2020-11-12 下午2:59
 */
@FunctionalInterface
public interface RedisLockTaskInterface<T> {

    /**
     * redisLock
     *
     * @return T
     */
    T redisLock();

}
