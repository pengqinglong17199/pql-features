package kfang.agent.feature.saas.request.limit;

import cn.hyugatool.extra.aop.AnnotationUtil;
import kfang.agent.feature.saas.parse.time.TimeParseFactory;
import kfang.agent.feature.saas.parse.time.core.TimeParse;
import kfang.agent.feature.saas.request.RequestCacheKey;
import kfang.agent.feature.saas.request.operatesystem.OperatorSystem;
import kfang.infra.common.cache.KfangCache;
import kfang.infra.common.cache.cachekey.CacheKeyType;
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

    @Pointcut("@annotation(AgentRequestLimit)")
    private void pointCutMethodService() {
    }

    BaseController baseController = new BaseController();

    @Resource(name = "agentCache")
    private KfangCache agentCache;

    @Before("pointCutMethodService()")
    public void before(JoinPoint joinPoint) {

        LoginDto currentPerson = baseController.getCurrentPerson();

        String id = currentPerson.getId();

        AgentRequestLimit requestLimit = AnnotationUtil.getDeclaredAnnotation(joinPoint, AgentRequestLimit.class);

        try {
            // 当前计数
            String name = requestLimit.name();
            long ttl = agentCache.ttl(RequestCacheKey.LIMIT, String.format("%s:%s", id, name));

            // 如果没有 解析time获取缓存时间 存入缓存次数1
            if (ttl == -2) {
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
        }finally {
            // 清除需要清理的缓存
            String[] removes = requestLimit.removes();
            for(String removeName : removes){
                agentCache.remove(RequestCacheKey.LIMIT, String.format("%s:%s", id, removeName));
            }
        }

    }
}