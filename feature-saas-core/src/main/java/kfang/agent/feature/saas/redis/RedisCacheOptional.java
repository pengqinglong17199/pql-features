// package kfang.agent.feature.saas.redis;
//
// import cn.hyugatool.core.object.ObjectUtil;
// import com.kfang.common.agent.func.redis.RedisCacheInterface;
// import com.kfang.common.agent.func.redis.RedisCacheOptionalInterface;
//
// /**
//  * RedisCacheOptional
//  *
//  * @author hyuga
//  * @since 2021/2/8
//  */
// public final class RedisCacheOptional<T> {
//
//     private T object;
//
//     private final String key;
//
//     private final RedisCacheInterface<T> redisCacheInterface;
//
//     private RedisCacheOptional(RedisCacheInterface<T> redisCacheInterface, String key) {
//         this.redisCacheInterface = redisCacheInterface;
//         this.key = key;
//     }
//
//     public static <T> RedisCacheOptional<T> init(RedisCacheInterface<T> redisCacheInterface, String key) {
//         return new RedisCacheOptional<>(redisCacheInterface, key);
//     }
//
//     public RedisCacheOptional<T> getCache() {
//         object = redisCacheInterface.get(key);
//         return this;
//     }
//
//     public RedisCacheOptional<T> orElse(RedisCacheOptionalInterface<T> redisCacheOptionalInterface) {
//         if (ObjectUtil.isNull(object)) {
//             object = redisCacheOptionalInterface.execute();
//         }
//         return this;
//     }
//
//     public RedisCacheOptional<T> orElse(T objectParameter) {
//         if (ObjectUtil.isNull(object)) {
//             object = objectParameter;
//         }
//         return this;
//     }
//
//     public RedisCacheOptional<T> putCache() {
//         redisCacheInterface.put(key, object);
//         return this;
//     }
//
//     public RedisCacheOptional<T> putCache(int expire) {
//         redisCacheInterface.put(key, object, expire);
//         return this;
//     }
//
//     public T run() {
//         return object;
//     }
//
// }
