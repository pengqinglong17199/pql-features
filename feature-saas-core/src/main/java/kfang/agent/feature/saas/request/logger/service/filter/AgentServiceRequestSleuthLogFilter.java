package kfang.agent.feature.saas.request.logger.service.filter;

import cn.hyugatool.bean.servlet.BodyReaderHttpServletRequestWrapper;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.extra.web.CommonWebUtil;
import cn.hyugatool.json.JsonUtil;
import cn.hyugatool.json.JsonValidateUtil;
import com.alibaba.fastjson.JSONObject;
import kfang.agent.feature.saas.constants.SaasConstants;
import kfang.agent.feature.saas.logger.util.LogUtil;
import kfang.infra.common.model.LoginDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static kfang.agent.feature.saas.logger.util.OperatorInfoUtil.*;

/**
 * AgentServiceRequestSleuthLogFilter
 * 服务端链路日志过滤器，配合logback.xml使用
 *
 * @author hyuga
 * @since 2021/10/28
 */
@Slf4j
@Component
public class AgentServiceRequestSleuthLogFilter extends OncePerRequestFilter {

    private static final String OPERATOR_IP = "operatorIp";
    private static final String OPERATOR_SYSTEM = "operatorSystem";

    private static final String OPERATOR_INFO = "操作人信息";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        BodyReaderHttpServletRequestWrapper wrapperRequest = new BodyReaderHttpServletRequestWrapper(request);
        try {
            initSleuthInfo(wrapperRequest);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error(log, OPERATOR_INFO, "service层提取操作人信息异常", e.getMessage(), e);
        }
        filterChain.doFilter(wrapperRequest, response);
    }

    public static void initSleuthInfo(BodyReaderHttpServletRequestWrapper wrapperRequest) {
        if (ObjectUtil.isNull(wrapperRequest)) {
            return;
        }

        String jsonParams = CommonWebUtil.getJsonParams(wrapperRequest);
        if (StringUtil.isEmpty(jsonParams)) {
            return;
        }
        final String requestUrl = wrapperRequest.getRequestURL().toString();
        if (JsonValidateUtil.isBadJson(jsonParams)) {
            LogUtil.error(log, OPERATOR_INFO, "POST入参异常", String.format("requestURL:%s,params:%s", requestUrl, jsonParams));
            return;
        }
        JSONObject jsonObject = JsonUtil.parseObject(jsonParams);
        String loginExtendDtoStr = jsonObject.getString(SaasConstants.LOGIN_EXTEND_DTO);
        if (StringUtil.isEmpty(loginExtendDtoStr)) {
            MDC.put(LOG_PLATFORM_ORG_ID, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_ID, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_USERNAME, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_ORG_ID, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_ORG_NAME, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_ORG_POSITION_ID, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_ORG_POSITION_NAME, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_COMPANY_ORG_ID, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_COMPANY_ORG_NAME, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_IS_MAJOR, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_IP, StringUtil.EMPTY);
            MDC.put(LOG_OPERATOR_TERMINAL_TYPE, StringUtil.EMPTY);
            return;
        }

        LoginDto loginDto = JsonUtil.toJavaObject(JsonUtil.toJsonString(loginExtendDtoStr), LoginDto.class);

        MDC.put(LOG_PLATFORM_ORG_ID, loginDto.getPlatformOrgId());
        MDC.put(LOG_OPERATOR_ID, loginDto.getId());
        MDC.put(LOG_OPERATOR_USERNAME, loginDto.getName());
        MDC.put(LOG_OPERATOR_ORG_ID, loginDto.getOrgId());
        MDC.put(LOG_OPERATOR_ORG_NAME, loginDto.getOrgName());
        MDC.put(LOG_OPERATOR_ORG_POSITION_ID, loginDto.getPositionId());
        MDC.put(LOG_OPERATOR_ORG_POSITION_NAME, loginDto.getPositionName());
        MDC.put(LOG_OPERATOR_COMPANY_ORG_ID, loginDto.getCompanyOrgId());
        MDC.put(LOG_OPERATOR_COMPANY_ORG_NAME, loginDto.getCompanyOrgName());
        MDC.put(LOG_OPERATOR_IS_MAJOR, loginDto.getIsMajor());
        MDC.put(LOG_OPERATOR_IP, jsonObject.getString(OPERATOR_IP));
        MDC.put(LOG_OPERATOR_TERMINAL_TYPE, jsonObject.getString(OPERATOR_SYSTEM));
    }

}