package kfang.agent.feature.saas.export;

import cn.hyugatool.extra.aop.AopUtil;
import kfang.infra.common.cache.KfangCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * AgentExportLimitAspect
 *
 * @author fjk
 * @since 2021/8/11
 */
@Slf4j
@Aspect
@Component
public class AgentExportLimitAspect {

    @Resource(name = "agentCache")
    private KfangCache agentCache;

    @Pointcut("@annotation(AgentExportLimit)")
    private void cutMethod() {
    }

    /**
     * 后置/最终通知：无论目标方法在执行过程中出现异常都会在它之后调用
     */
    @After("cutMethod()")
    public void after(JoinPoint joinPoint) throws Throwable {
        AgentExportLimit annotation = AopUtil.getDeclaredAnnotation(joinPoint, AgentExportLimit.class);
        String key = annotation.key();
        long ttl = agentCache.ttl(ExportCacheKey.LIMIT, key);
        if (-2 != ttl) {
            if (ttl < 0) {
                agentCache.remove(ExportCacheKey.LIMIT, key);
            } else {
                agentCache.decr(ExportCacheKey.LIMIT, key, 1L, Integer.valueOf(String.valueOf(ttl)));
            }
        }
    }

    @Around("cutMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        AgentExportLimit annotation = AopUtil.getDeclaredAnnotation(joinPoint, AgentExportLimit.class);
        String key = annotation.key();

        int exportCount = annotation.exportCount();
        int timeSeconds = annotation.timeSeconds();
        long exportKet = agentCache.getIncr(ExportCacheKey.LIMIT, key);
        if (exportKet == 0) {
            agentCache.incr(ExportCacheKey.LIMIT, key, 1L, timeSeconds);
        } else if (exportKet > exportCount || exportKet < 0) {
            agentCache.remove(ExportCacheKey.LIMIT, key);
            // 报错
            throw new RuntimeException("当前导出人数过多，请稍后再试");
        } else {
            agentCache.incr(ExportCacheKey.LIMIT, key);
        }

        // 执行源方法
        return joinPoint.proceed();
    }

}