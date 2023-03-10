package kfang.agent.feature.saas.export;

import cn.hyugatool.aop.AnnotationUtil;
import cn.hyugatool.aop.aspectj.AspectAroundInject;
import cn.hyugatool.aop.aspectj.AspectInject;
import kfang.infra.common.cache.KfangCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
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
public class AgentExportLimitAspect implements AspectInject, AspectAroundInject {

    @Resource(name = "agentCache")
    private KfangCache agentCache;

    @Override
    @Pointcut("@annotation(AgentExportLimit)")
    public void pointcut() {

    }

    @Override
    public void before(JoinPoint joinPoint) {

    }

    @Override
    public void afterReturning(JoinPoint joinPoint, Object result) {

    }

    @Override
    public void after(JoinPoint joinPoint) {
        AgentExportLimit annotation = AnnotationUtil.getDeclaredAnnotation(joinPoint, AgentExportLimit.class);
        String key = annotation.key();
        long ttl = agentCache.ttl(ExportCacheKey.LIMIT, key);
        int i = -2;
        if (i != ttl) {
            if (ttl < 0) {
                agentCache.remove(ExportCacheKey.LIMIT, key);
            } else {
                agentCache.decr(ExportCacheKey.LIMIT, key, 1L, Integer.valueOf(String.valueOf(ttl)));
            }
        }
    }

    @Override
    public void afterThrowing(Throwable exception) {

    }

    @Override
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        AgentExportLimit annotation = AnnotationUtil.getDeclaredAnnotation(joinPoint, AgentExportLimit.class);
        String key = annotation.key();

        int exportCount = annotation.exportCount();
        int timeSeconds = annotation.timeSeconds();
        long exportKet = agentCache.getIncr(ExportCacheKey.LIMIT, key);
        if (exportKet == 0) {
            agentCache.incr(ExportCacheKey.LIMIT, key, 1L, timeSeconds);
        } else if (exportKet > exportCount || exportKet < 0) {
            agentCache.remove(ExportCacheKey.LIMIT, key);
            // ??????
            throw new RuntimeException("??????????????????????????????????????????");
        } else {
            agentCache.incr(ExportCacheKey.LIMIT, key);
        }
        // ???????????????
        return joinPoint.proceed();
    }

}