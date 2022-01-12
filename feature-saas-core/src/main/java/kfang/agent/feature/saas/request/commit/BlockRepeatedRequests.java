package kfang.agent.feature.saas.request.commit;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.crypto.MD5Util;
import cn.hyugatool.extra.aop.AopUtil;
import com.alibaba.fastjson.JSONObject;
import kfang.agent.feature.saas.request.commit.annotations.RepeatedRequests;
import kfang.infra.api.RequestResultException;
import kfang.infra.api.validate.extend.OperateExtendForm;
import kfang.infra.common.cache.CacheOpeType;
import kfang.infra.common.cache.KfangCache;
import kfang.infra.common.cache.cachekey.CacheKeyType;
import kfang.infra.common.cache.redis.RedisAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 锁住重复请求
 *
 * @author pengqinglong
 * @since 2021/10/11
 */
@Slf4j
@Aspect
@Component
public class BlockRepeatedRequests {

    @Resource(name = "agentCache")
    private KfangCache agentCache;

    @Pointcut("@annotation(kfang.agent.feature.saas.request.commit.annotations.RepeatedRequests)")
    private void cutMethod() {
    }

    /**
     * 前置通知：在目标方法执行前调用
     */
    @Around("cutMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        RepeatedRequests repeatedRequests = AopUtil.getDeclaredAnnotation(joinPoint, RepeatedRequests.class);

        // 无注解
        if (repeatedRequests == null) {
            return joinPoint.proceed();
        }

        List<String> paramJson = ListUtil.newArrayList();

        // 有注解则判断是否有OperateExtendForm参数
        OperateExtendForm form = null;
        for (Object arg : ListUtil.optimize(joinPoint.getArgs())) {

            paramJson.add(JSONObject.toJSONString(arg));

            if (arg instanceof OperateExtendForm) {
                form = (OperateExtendForm) arg;
            }
        }
        // 无OperateExtendForm参数 直接执行 通过拦截器进行ip阻断
        if (form == null) {
            return joinPoint.proceed();
        }


        String md5 = MD5Util.getMD5(form.getOperatorId() + paramJson);
        // 生成lockKey 请求类名-方法名：操作人-md5(操作人id+请求参数)
        String key = String.format("%s-%s:%s-%s:", joinPoint.getTarget().getClass().getName(), joinPoint.getSignature().getName(),  form.getOperatorId(), md5);

        String lockKey = agentCache.wrapKey(CacheKey.REPEATED_REQUEST, key);
        boolean flag = (boolean) agentCache.<RedisAction>doCustomAction(CacheOpeType.SAVE, redisTemplate -> redisTemplate.opsForValue().setIfAbsent(lockKey, 1, repeatedRequests.time(), TimeUnit.MILLISECONDS));
        if (!flag) {
            throw new RequestResultException("点的太快了哦");
        }

        return joinPoint.proceed();
    }

    @AllArgsConstructor
    private enum CacheKey implements CacheKeyType {
        /**
         * 重复请求key
         */
        REPEATED_REQUEST("COMMON");

        @Getter
        private final String baseType;

        @Override
        public String getCacheKey() {
            return this.name();
        }
    }

}