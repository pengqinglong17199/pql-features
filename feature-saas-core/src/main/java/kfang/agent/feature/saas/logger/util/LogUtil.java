package kfang.agent.feature.saas.logger.util;

import cn.hyugatool.core.collection.ArrayUtil;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.string.StringUtil;
import kfang.agent.feature.saas.logger.LogLevel;
import kfang.infra.common.KfangInfraCommonProperties;
import kfang.infra.common.spring.SpringBeanPicker;
import org.slf4j.Logger;
import org.springframework.lang.NonNull;

/**
 * AgentLogger
 *
 * @author hyuga
 * @since 2021/3/25
 */
public class LogUtil {

    /**
     * [TAG][服务名][归属模块][业务操作][文本描述]
     */
    private static final String LOG_TEMPLATE = "[{}][{}][{}][{}]";

    private static final String KFANG = "kfang";

    public static void debug(Logger log, String module, String operateType) {
        log(log, LogLevel.DEBUG, module, operateType, null, null);
    }

    public static void debug(Logger log, String module, String operateType, Object content) {
        log(log, LogLevel.DEBUG, module, operateType, content, null);
    }

    public static void info(Logger log, String module, String operateType) {
        log(log, LogLevel.INFO, module, operateType, null, null);
    }

    public static void info(Logger log, String module, String operateType, Object content) {
        log(log, LogLevel.INFO, module, operateType, content, null);
    }

    public static void warn(Logger log, String module, String operateType) {
        log(log, LogLevel.WARN, module, operateType, null, null);
    }

    public static void warn(Logger log, String module, String operateType, Throwable e) {
        log(log, LogLevel.WARN, module, operateType, null, e);
    }

    public static void warn(Logger log, String module, String operateType, Object content) {
        log(log, LogLevel.WARN, module, operateType, content, null);
    }

    public static void warn(Logger log, String module, String operateType, Object content, Throwable e) {
        log(log, LogLevel.WARN, module, operateType, content, e);
    }

    public static void error(Logger log, String module, String operateType) {
        log(log, LogLevel.ERROR, module, operateType, null, null);
    }

    public static void error(Logger log, String module, String operateType, Throwable e) {
        log(log, LogLevel.ERROR, module, operateType, null, e);
    }

    public static void error(Logger log, String module, String operateType, Object content) {
        log(log, LogLevel.ERROR, module, operateType, content, null);
    }

    public static void error(Logger log, String module, String operateType, Object content, Throwable e) {
        log(log, LogLevel.ERROR, module, operateType, content, e);
    }

    private static void log(@NonNull Logger log, @NonNull LogLevel logType, @NonNull String module,
                            String operateType, Object content, Throwable e) {
        String logTemplate = LOG_TEMPLATE;
        if (StringUtil.isEmpty(content)) {
            logTemplate = LOG_TEMPLATE.substring(0, LOG_TEMPLATE.length() - 4);
        }
        if (StringUtil.isEmpty(operateType)) {
            throw new RuntimeException("日志操作类型不能为空");
        }

        String serverName = SpringBeanPicker.getBean(KfangInfraCommonProperties.class).getEnv().getAppName().toUpperCase();

        switch (logType) {
            case DEBUG -> log.debug(logTemplate, serverName, module, operateType, content);
            case INFO -> log.info(logTemplate, serverName, module, operateType, content);
            case WARN -> {
                if (ObjectUtil.isNull(e)) {
                    log.warn(logTemplate, serverName, module, operateType, content);
                } else {
                    log.warn(logTemplate, serverName, module, operateType, content, e);
                }
            }
            case ERROR -> {
                if (ObjectUtil.isNull(e)) {
                    log.error(logTemplate, serverName, module, operateType, content);
                } else {
                    log.error(logTemplate, serverName, module, operateType, content, e);
                    printStackTrace(serverName, log, module, e.getStackTrace());
                }
            }
        }
    }

    /**
     * 打印堆栈日志
     */
    private static void printStackTrace(String serverName, Logger log, String module, StackTraceElement[] stackTrace) {
        if (ArrayUtil.isEmpty(stackTrace)) {
            return;
        }
        int maximumFetchLevel = Math.min(stackTrace.length, 10);
        for (int i = 1; i < maximumFetchLevel; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            if (ObjectUtil.isNull(stackTraceElement)) {
                continue;
            }
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            int lineNumber = stackTraceElement.getLineNumber();
            if (className.contains(KFANG)) {
                String errorMsg = String.format("class:%s,method:%s,lineNumber:%s", className, methodName, lineNumber);
                OperatorInfoUtil.logOperatorInfo(LogLevel.ERROR);
                log.error(LOG_TEMPLATE, serverName, module, "异常调用链", errorMsg);
                return;
            }
        }
    }

}
