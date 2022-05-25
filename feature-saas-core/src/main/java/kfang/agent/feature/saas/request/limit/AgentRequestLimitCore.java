package kfang.agent.feature.saas.request.limit;

import cn.hyugatool.aop.annotation.AnnotationUtil;
import kfang.agent.feature.saas.parse.time.TimeParseFactory;
import kfang.agent.feature.saas.parse.time.core.TimeParse;
import kfang.agent.feature.saas.request.RequestCacheKey;
import kfang.infra.common.cache.KfangCache;
import kfang.infra.common.model.LoginDto;
import kfang.infra.web.controller.BaseController;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 请求次数限制核心类
 *
 * @author pengqinglong
 * @since 2022/2/15
 */
@RefreshScope
@Aspect
@Component
public class AgentRequestLimitCore {

    BaseController baseController = new BaseController();

    @Resource(name = "agentCache")
    private KfangCache agentCache;

    @Pointcut("@annotation(AgentRequestLimit)")
    private void pointCutAgentRequestLimit() {
    }

    @Before("pointCutAgentRequestLimit()")
    public void agentRequestLimit(JoinPoint joinPoint) {

        LoginDto currentPerson = baseController.getCurrentPerson();

        String id = currentPerson.getId();

        AgentRequestLimit requestLimit = AnnotationUtil.getDeclaredAnnotation(joinPoint, AgentRequestLimit.class);

        this.handle(id, requestLimit);
    }


    @Pointcut("@annotation(AgentRequestLimits)")
    private void pointCutAgentRequestLimits() {
    }

    @Before("pointCutAgentRequestLimits()")
    public void before(JoinPoint joinPoint) {

        LoginDto currentPerson = baseController.getCurrentPerson();

        String id = currentPerson.getId();

        AgentRequestLimits requestLimit = AnnotationUtil.getDeclaredAnnotation(joinPoint, AgentRequestLimits.class);

        AgentRequestLimit[] value = requestLimit.value();
        for (AgentRequestLimit agentRequestLimit : value) {
            this.handle(id, agentRequestLimit);
        }
    }

    /**
     * 核心处理方法
     */
    private void handle(String id, AgentRequestLimit requestLimit) {
        try {
            // 当前计数
            String name = requestLimit.name();
            long ttl = agentCache.ttl(RequestCacheKey.LIMIT, String.format("%s:%s", id, name));

            // 如果没有 解析time获取缓存时间 存入缓存次数1
            int i = -2;
            if (ttl == i) {
                TimeParse timeParse = TimeParseFactory.create(requestLimit.time());
                Long parse = timeParse.parse(requestLimit.time());
                agentCache.put(RequestCacheKey.LIMIT, String.format("%s:%s", id, name), 1, parse.intValue());
                return;
            }

            // 对比计数
            Integer count = (Integer) agentCache.get(RequestCacheKey.LIMIT, String.format("%s:%s", id, name));
            long limit = requestLimit.limit();
            if (limit < count) {
                throw new RequestLimitException(requestLimit.message(), requestLimit.code());
            }
            agentCache.put(RequestCacheKey.LIMIT, String.format("%s:%s", id, name), count + 1, (int) ttl);
        } finally {
            // 清除需要清理的缓存
            String[] removes = requestLimit.removes();
            for (String removeName : removes) {
                agentCache.remove(RequestCacheKey.LIMIT, String.format("%s:%s", id, removeName));
            }
        }
    }

}