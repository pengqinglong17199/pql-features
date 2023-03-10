package kfang.agent.feature.saas.logger.util;

import cn.hyugatool.core.string.StringUtil;
import kfang.agent.feature.saas.logger.LogLevel;
import kfang.agent.feature.saas.logger.LogModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * 操作人信息工具类
 *
 * @author hyuga
 * @since 2021-10-29
 */
@Slf4j
public class OperatorInfoUtil {

    public static final String LOG_PLATFORM_ORG_ID = "platform-org-id";
    public static final String LOG_OPERATOR_ID = "operator-id";
    public static final String LOG_OPERATOR_USERNAME = "operator-username";
    public static final String LOG_OPERATOR_ORG_ID = "operator-org-id";
    public static final String LOG_OPERATOR_ORG_NAME = "operator-org-name";
    public static final String LOG_OPERATOR_ORG_POSITION_ID = "operator-org-position-id";
    public static final String LOG_OPERATOR_ORG_POSITION_NAME = "operator-org-position-name";
    public static final String LOG_OPERATOR_COMPANY_ORG_ID = "operator-company-org-id";
    public static final String LOG_OPERATOR_COMPANY_ORG_NAME = "operator-company-org-name";
    public static final String LOG_OPERATOR_IS_MAJOR = "operator-is-major";
    public static final String LOG_OPERATOR_IP = "operator-ip";
    public static final String LOG_OPERATOR_TERMINAL_TYPE = "operator-terminal-type";
    public static final String LOG_OPERATOR_PLATFORM = "operator-platform";
    public static final String LOG_OPERATOR_APP_VERSION = "operator-app-version";

    public static final String LOG_OPERATOR_INFO_TEMPLATE = "" +
            "ip:%s," +
            "terminalType:%s," +
            "x-platform:%s," +
            "x-source-version:%s," +
            "platformOrgId:%s," +
            "id:%s," +
            "name:%s," +
            "orgId:%s," +
            "orgName:%s," +
            "positionId:%s," +
            "positionName:%s," +
            "companyOrgId:%s," +
            "companyOrgName:%s," +
            "isMajor:%s";

    @SuppressWarnings("unused")
    public static void logOperatorInfo() {
        logOperatorInfo(LogLevel.INFO);
    }

    public static void logOperatorInfo(LogLevel logLevel) {
        final String platformOrgId = MDC.get(LOG_PLATFORM_ORG_ID);
        final String operatorId = MDC.get(LOG_OPERATOR_ID);
        final String operatorUsername = MDC.get(LOG_OPERATOR_USERNAME);
        final String operatorOrgId = MDC.get(LOG_OPERATOR_ORG_ID);
        final String operatorOrgName = MDC.get(LOG_OPERATOR_ORG_NAME);
        final String operatorOrgPositionId = MDC.get(LOG_OPERATOR_ORG_POSITION_ID);
        final String operatorOrgPositionName = MDC.get(LOG_OPERATOR_ORG_POSITION_NAME);
        final String operatorCompanyOrgId = MDC.get(LOG_OPERATOR_COMPANY_ORG_ID);
        final String operatorCompanyOrgName = MDC.get(LOG_OPERATOR_COMPANY_ORG_NAME);
        final String operatorIsMajor = MDC.get(LOG_OPERATOR_IS_MAJOR);
        final String operatorIp = MDC.get(LOG_OPERATOR_IP);
        final String operatorTerminalType = MDC.get(LOG_OPERATOR_TERMINAL_TYPE);
        final String operatorPlatform = MDC.get(LOG_OPERATOR_PLATFORM);
        final String operatorAppVersion = MDC.get(LOG_OPERATOR_APP_VERSION);

        if (StringUtil.isEmpty(operatorId)) {
            return;
        }

        String operatorInfo = String.format(LOG_OPERATOR_INFO_TEMPLATE,
                operatorIp, operatorTerminalType, operatorPlatform, operatorAppVersion, platformOrgId, operatorId, operatorUsername, operatorOrgId, operatorOrgName,
                operatorOrgPositionId, operatorOrgPositionName, operatorCompanyOrgId, operatorCompanyOrgName, operatorIsMajor);

        // noinspection AlibabaSwitchStatement
        switch (logLevel) {
            case DEBUG -> LogUtil.debug(log, LogModuleEnum.OPERATOR_INFO.getDesc(), operatorInfo);
            case INFO -> LogUtil.info(log, LogModuleEnum.OPERATOR_INFO.getDesc(), operatorInfo);
            case WARN -> LogUtil.warn(log, LogModuleEnum.OPERATOR_INFO.getDesc(), operatorInfo);
            case ERROR -> LogUtil.error(log, LogModuleEnum.OPERATOR_INFO.getDesc(), operatorInfo);
            default -> throw new IllegalStateException("Unexpected value: " + logLevel);
        }
    }

    @AllArgsConstructor
    enum LogModuleEnum implements LogModule {
        /**
         *
         */
        OPERATOR_INFO("操作人信息");
        @Getter
        private final String desc;
    }

}
