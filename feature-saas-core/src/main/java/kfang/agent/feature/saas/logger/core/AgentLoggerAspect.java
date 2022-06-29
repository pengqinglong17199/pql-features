package kfang.agent.feature.saas.logger.core;

import cn.hyugatool.aop.AnnotationUtil;
import cn.hyugatool.aop.aspectj.AspectAroundInject;
import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.date.DateFormat;
import cn.hyugatool.core.date.DateUtil;
import cn.hyugatool.core.date.interval.TimeInterval;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.core.string.snow.SnowflakeIdUtil;
import cn.hyugatool.json.JsonUtil;
import kfang.agent.feature.saas.logger.annotations.AgentLogger;
import kfang.agent.feature.saas.logger.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Agent请求响应日志切面
 *
 * @author pengqinglong
 * Create on 2021-04-10
 */
@Slf4j
@Aspect
@Component
public class AgentLoggerAspect implements AspectAroundInject {

    @Override
    @Pointcut("@annotation(kfang.agent.feature.saas.logger.annotations.AgentLogger)")
    public void pointcut() {

    }

    @Override
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        final String snowflakeId = SnowflakeIdUtil.getSnowflakeIdStr();

        AgentLogger agentServiceLog = AnnotationUtil.getDeclaredAnnotation(joinPoint, AgentLogger.class);
        String module = agentServiceLog.logModule();
        Class<?> clazz = agentServiceLog.clazz();

        Logger logger = clazz == AgentLogger.class ? log : LoggerFactory.getLogger(clazz);

        Object[] args = joinPoint.getArgs();
        List<Object> canSerializableArgs = ListUtil.optimize(args).stream()
                .filter(arg -> arg instanceof Serializable)
                .filter(arg -> !(arg instanceof MultipartFile)).collect(Collectors.toList());

        String params;
        if (agentServiceLog.loginDto()) {
            params = JsonUtil.toJsonString(canSerializableArgs);
        } else {
            // FIXME: 2022/6/29 修复该方法
            params = JsonUtil.toJsonString(canSerializableArgs/*, LOGIN_EXTEND_DTO*/);
        }

        String startTime = DateUtil.format(DateFormat.yyyy_MM_dd_HH_mm_ss_SSS, DateUtil.now());

        String classMethod = joinPoint.getSignature().toShortString();
        LogUtil.info(logger, module, StringUtil.format("request time: [{}], method: [{}]", startTime, classMethod));
        if (agentServiceLog.logParams()) {
            LogUtil.info(logger, module, StringUtil.format("request form:{}", params));
        }

        TimeInterval timeInterval = new TimeInterval();
        timeInterval.start(snowflakeId);

        try {
            Object proceed = joinPoint.proceed();
            if (ObjectUtil.nonNull(proceed) && agentServiceLog.logResult()) {
                String result = JsonUtil.toJsonString(proceed);
                LogUtil.info(logger, module, String.format("response result: %s", result));
            }
            return proceed;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            LogUtil.info(logger, module, "警告：有异常信息，请查阅[[[error.log]]]");
            LogUtil.error(logger, module, String.format("response exception: %s", throwable.getMessage()), throwable);
            throw throwable;
        } finally {
            String endTime = DateUtil.format(DateFormat.yyyy_MM_dd_HH_mm_ss_SSS, DateUtil.now());
            LogUtil.info(logger, module, String.format("response time: [%s], 响应耗时: [%s]", endTime, timeInterval.intervalPretty(snowflakeId)));
        }
    }

}
