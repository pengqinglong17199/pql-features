package kfang.agent.feature.dingtalk.core;

import cn.hyugatool.aop.AnnotationUtil;
import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.system.NetworkUtil;
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

import java.util.List;

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

    /**
     * 一个线程调用的一整个方法链 只发送一次异常告警
     */
    private final ThreadLocal<Boolean> threadLocal = new InheritableThreadLocal<>();

    @Pointcut("@annotation(kfang.agent.feature.dingtalk.annotations.AgentDingTalk)")
    private void cutMethod() {

    }

    @Around("cutMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        try {
            // 线程刚进来 先remove 清空标志位 防止内存泄露
            threadLocal.remove();

            // 防止线程复用情况下拉到线程上一次异常设置的true 所以设置一次false
            threadLocal.set(false);

            // 正常执行 异常才需要处理
            return joinPoint.proceed();

        } catch (Throwable e) {

            // 检测是否下级方法已经发送警告
            if (threadLocal.get()) {
                // 直接抛出异常
                throw e;
            }

            // 第一次也就是最内层方法报错 设置属性
            threadLocal.set(true);

            // 获取方法上的注解
            AgentDingTalk annotation = AnnotationUtil.getDeclaredAnnotation(joinPoint, AgentDingTalk.class);

            // 拿到方法作者
            Author[] authors = annotation.authors();

            // 组装内容
            String title = String.format("当前环境 : %s \n异常服务 : %s \n所属类名 : %s \n方法名称 : %s \n研发人员 : %s",
                    DingTalkConfiguration.getDeploy().toUpperCase() + " [" + NetworkUtil.getLocalIpAddr() + "]",
                    DingTalkConfiguration.getServiceName().toUpperCase(),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    ListUtil.flat(ListUtil.optimize(authors), Author::getName));
            String context = String.format("异常信息 : %s", e.getMessage());

            // 发送钉钉消息
            List<Author> list = ListUtil.optimize(DingTalkConfiguration.PRO_AUTHORS);
            for (Author author : authors) {
                if (!list.contains(author)) {
                    list.add(author);
                }
            }

            DingTalkUtil.builder()
                    .atMobiles(DingTalkConfiguration.isDev() ? authors : list.toArray(new Author[0]))
                    .title(title)
                    .context(context)
                    .send();

            // 抛出异常
            throw e;
        }

    }
}