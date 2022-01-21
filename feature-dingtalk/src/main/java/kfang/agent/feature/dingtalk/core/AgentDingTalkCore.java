package kfang.agent.feature.dingtalk.core;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.extra.aop.AopUtil;
import kfang.agent.feature.dingtalk.annotations.AgentDingTalk;
import kfang.agent.feature.dingtalk.config.DingTalkConfiguration;
import kfang.agent.feature.dingtalk.enums.Author;
import kfang.agent.feature.dingtalk.util.DingTalkUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;

/**
 * AgentDingTalkCore
 *
 * @author pengqinglong
 * @since 2022/1/21
 */
@Component
@Aspect
@Slf4j
public class AgentDingTalkCore {

    @Resource
    private DingTalkConfiguration dingTalkConfiguration;

    /**
     * 一个线程调用的一整个方法链 只发送一次异常告警
     */
    private final ThreadLocal<Boolean> threadLocal = new InheritableThreadLocal<>();

    @Pointcut("@annotation(kfang.agent.feature.dingtalk.annotations.AgentDingTalk)")
    private void cutMethod() {

    }

    @Around("cutMethod()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {

        try{
            // 线程刚进来 先remove 清空标志位 防止内存泄露
            threadLocal.remove();

            // 防止线程复用情况下拉到线程上一次的true 所以设置一次false
            threadLocal.set(false);

            // 正常执行 异常才需要处理
            joinPoint.proceed();

        }catch (Throwable e){

            // 检测是否下级方法已经发送警告
            if(threadLocal.get()){
                return;
            }

            // 第一次也就是最内层方法报错 设置属性
            threadLocal.set(true);

            // 获取方法上的注解
            AgentDingTalk annotation = AopUtil.getDeclaredAnnotation(joinPoint, AgentDingTalk.class);

            // 拿到方法作者
            Author[] authors = annotation.authors();

            // 组装内容
            String title = String.format("发生异常 服务名:[%s] 方法名:[%s] 作者:%s",
                    DingTalkConfiguration.getServiceName(),
                    joinPoint.getSignature().getName(),
                    ListUtil.flat(ListUtil.optimize(authors), Author::getName));
            String context = String.format("所属class类：[%s] <br> 异常信息：%s", joinPoint.getSignature().getDeclaringTypeName(), e.getMessage());

            // 发送钉钉消息
            DingTalkUtil.builder()
                    .atMobiles(DingTalkConfiguration.isDev() ? authors : DingTalkConfiguration.PRO_AUTHORS)
                    .title(title)
                    .context(context)
                    .send();

            // 抛出异常
            throw e;
        }

    }
}