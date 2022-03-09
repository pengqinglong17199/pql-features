package kfang.agent.feature.saas.export;

import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.extra.aop.AopUtil;
import io.swagger.annotations.ApiModelProperty;
import kfang.infra.common.cache.KfangCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
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
     * 前置通知：在目标方法执行前调用
     */
    @Before("cutMethod()")
    public void begin() {
    }

    /**
     * 后置通知：在目标方法执行后调用，若目标方法出现异常，则不执行
     */
    @AfterReturning("cutMethod()")
    public void afterReturning() {
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

    /**
     * 异常通知：目标方法抛出异常时执行
     */
    @AfterThrowing("cutMethod()")
    public void afterThrowing() {
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