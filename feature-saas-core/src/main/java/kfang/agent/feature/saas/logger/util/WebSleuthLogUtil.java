package kfang.agent.feature.saas.logger.util;

import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.extra.web.IpAddressUtil;
import kfang.infra.common.model.LoginDto;
import kfang.infra.web.constants.WebCommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;

import static kfang.agent.feature.saas.logger.util.OperatorInfoUtil.*;

/**
 * WebSleuthLogUtil
 *
 * @author hyuga
 */
@Slf4j
public class WebSleuthLogUtil {

    public static void initSleuthInfo(HttpServletRequest request) {
        LoginDto loginDto = ObjectUtil.cast(request.getSession().getAttribute(WebCommonConstants.CURRENT_LOGIN_PERSON));
        if (ObjectUtil.nonNull(loginDto)) {
            MDC.put(LOG_OPERATOR_ID, loginDto.getId());
            MDC.put(LOG_OPERATOR_USERNAME, loginDto.getName());
            MDC.put(LOG_OPERATOR_ORG_ID, loginDto.getOrgId());
            MDC.put(LOG_OPERATOR_ORG_NAME, loginDto.getOrgName());
            MDC.put(LOG_OPERATOR_ORG_POSITION_ID, loginDto.getPositionId());
            MDC.put(LOG_OPERATOR_ORG_POSITION_NAME, loginDto.getPositionName());
            MDC.put(LOG_OPERATOR_COMPANY_ORG_ID, loginDto.getCompanyOrgId());
            MDC.put(LOG_OPERATOR_COMPANY_ORG_NAME, loginDto.getCompanyOrgName());
            MDC.put(LOG_OPERATOR_IS_MAJOR, loginDto.getIsMajor());
            MDC.put(LOG_OPERATOR_IP, IpAddressUtil.getIpAddr(request)[0]);
            MDC.put(LOG_OPERATOR_TERMINAL_TYPE, loginDto.getTerminalType());
        } else {
            MDC.put(LOG_OPERATOR_ID, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_USERNAME, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_ORG_ID, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_ORG_NAME, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_ORG_POSITION_ID, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_ORG_POSITION_NAME, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_COMPANY_ORG_ID, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_COMPANY_ORG_NAME, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_IS_MAJOR, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_IP, IpAddressUtil.getIpAddr(request)[0]);
            MDC.put(LOG_OPERATOR_TERMINAL_TYPE, StringUtil.EMPTY);
        }
    }

}