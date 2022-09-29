package kfang.agent.feature.saas.request.logger.web.filter;

import cn.hyugatool.core.constants.HyugaConstants;
import cn.hyugatool.core.io.bytes.ByteSizeConvert;
import cn.hyugatool.core.lang.Console;
import cn.hyugatool.core.object.MapperUtil;
import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.extra.spring.AccessObjectUtil;
import cn.hyugatool.system.NetworkUtil;
import io.micrometer.core.lang.NonNull;
import kfang.agent.feature.saas.constants.FeignConstants;
import kfang.agent.feature.saas.enums.EnvironmentEnum;
import kfang.agent.feature.saas.request.logger.web.AgentWebRequestLogConfiguration;
import kfang.agent.feature.saas.request.logger.web.model.TerminalRequestModel;
import kfang.agent.feature.saas.request.logger.web.model.TerminalRequestSimpleModel;
import kfang.infra.common.KfangInfraCommonProperties;
import kfang.infra.common.spring.SpringBeanPicker;
import kfang.infra.web.common.util.CommonWebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.stream.Collectors;

/**
 * WebRequestLogFilter
 * web端请求日志过滤器
 *
 * @author hyuga
 * @since 2021/3/11
 */
@Slf4j
@Component
public class WebRequestLogFilter extends OncePerRequestFilter {

    private static final String ACTUATOR = "actuator";
    private static final String USER_AGENT = "user-agent";
    private static final String HOST = "host";
    private static final String SERVER_PORT = "server.port";
    private static final String MS = "ms";
    private static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd hh:mm:ss.SSS";
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!AgentWebRequestLogConfiguration.isRequest()) {
            filterChain.doFilter(request, response);
            return;
        }

        String envUpperCase = SpringBeanPicker.getBean(KfangInfraCommonProperties.class).getEnv().getDeploy().toUpperCase();

        EnvironmentEnum[] environmentEnum = AgentWebRequestLogConfiguration.env();
        boolean containsEnv = Arrays.stream(environmentEnum).map(EnvironmentEnum::name).collect(Collectors.toSet()).contains(envUpperCase);
        if (!containsEnv) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getRequestURI().contains(ACTUATOR)) {
            filterChain.doFilter(request, response);
            return;
        } else if (request.getRequestURI().contains(HyugaConstants.POINT)) {
            filterChain.doFilter(request, response);
            return;
        }

        long requestTime = System.currentTimeMillis();

        ContentCachingRequestWrapper wrapperRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrapperResponse = new ContentCachingResponseWrapper(response);
        String userAgent = wrapperRequest.getHeader(USER_AGENT);
        filterChain.doFilter(wrapperRequest, wrapperResponse);

        long responseTime = System.currentTimeMillis();

        threadPoolSaveLog(request, requestTime, wrapperRequest, wrapperResponse, userAgent, responseTime);
        wrapperResponse.copyBodyToResponse();
    }

    private void threadPoolSaveLog(HttpServletRequest request, long requestTime, ContentCachingRequestWrapper wrapperRequest, ContentCachingResponseWrapper wrapperResponse, String userAgent, long responseTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_SSS);

        String responseBody = AccessObjectUtil.getResponseBody(wrapperResponse);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(responseTime - requestTime);

        TerminalRequestModel terminalRequestModel = new TerminalRequestModel();
        terminalRequestModel.setRequestIp(CommonWebUtil.getIpAddr(request)[0]);
        terminalRequestModel.setRequestTime(simpleDateFormat.format(requestTime));
        terminalRequestModel.setRequestMethod(wrapperRequest.getMethod());
        terminalRequestModel.setRequestUrl(wrapperRequest.getRequestURI());
        terminalRequestModel.setServerDomain(wrapperRequest.getHeader(HOST));
        terminalRequestModel.setUserAgent(userAgent);
        terminalRequestModel.setServerIp(NetworkUtil.getLocalIpAddr());
        terminalRequestModel.setServerPort(SpringBeanPicker.getBean(Environment.class).getProperty(SERVER_PORT));
        terminalRequestModel.setEncodingType(wrapperResponse.getContentType());
        terminalRequestModel.setStatusCode(String.valueOf(wrapperResponse.getStatus()));
        terminalRequestModel.setResponseTime(simpleDateFormat.format(responseTime));
        terminalRequestModel.setCostTime(c.get(Calendar.MILLISECOND) + MS);
        terminalRequestModel.setRequestParams(String.format("[%s]", AccessObjectUtil.getRequestParams(wrapperRequest)));
        terminalRequestModel.setRequestBody(AccessObjectUtil.getRequestBody(wrapperRequest));
        terminalRequestModel.setResponseBody(responseBody);
        if (responseBody != null) {
            terminalRequestModel.setResponseContextSize(ByteSizeConvert.model().format(responseBody.getBytes().length));
        }

        log(terminalRequestModel);
    }

    public static void log(TerminalRequestModel parameter) {
        String requestMethod = parameter.getRequestMethod();
        if (!StringUtil.contains(requestMethod, new String[]{GET, POST, DELETE})) {
            return;
        }
        synchronized (WebRequestLogFilter.class) {
            TerminalRequestSimpleModel terminalRequestSimpleModel = MapperUtil.copy(parameter, TerminalRequestSimpleModel.class);
            String requestBody = terminalRequestSimpleModel.getRequestBody();
            if (StringUtil.hasText(requestBody)) {
                terminalRequestSimpleModel.setRequestBody(StringUtil.removeAllSpace(requestBody));
            }

            if (FeignConstants.isDeveloperLocalEnvironment(NetworkUtil.getLocalIpAddr())) {
                Console.cyanLog(StringUtil.repeat("=====", 100));
                Console.blueLog(StringUtil.formatString(terminalRequestSimpleModel));
                Console.cyanLog(StringUtil.repeat("=====", 100));
            } else {
                log.info(StringUtil.formatString(terminalRequestSimpleModel));
            }
        }
    }

}