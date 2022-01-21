package kfang.agent.feature.dingtalk.annotations;

import kfang.agent.feature.dingtalk.enums.Author;

import java.lang.annotation.*;

/**
 * 盘客钉钉异常告警注解
 *
 * @author 彭清龙
 * @since 7:12 下午 2021/3/22
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AgentDingTalk {

    /**
     * 当前方法作者
     */
    Author[] authors();

}
