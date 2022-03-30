package kfang.agent.feature.saas.request.logger.web.filter;

import cn.hyugatool.core.constants.HyugaConstants;
import cn.hyugatool.core.enums.ByteType;
import cn.hyugatool.core.io.unit.ByteUtil;
import cn.hyugatool.core.lang.Console;
import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.extra.spring.AccessObjectUtil;
import cn.hyugatool.system.NetworkUtil;
import kfang.agent.feature.saas.constants.FeignConstants;
import kfang.agent.feature.saas.enums.EnvironmentEnum;
import kfang.agent.feature.saas.request.logger.web.AgentWebRequestLogConfiguration;
import kfang.infra.common.KfangInfraCommonProperties;
import kfang.infra.common.spring.SpringBeanPicker;
import kfang.infra.web.common.util.CommonWebUtil;
import org.springframework.lang.NonNull;
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
import java.util.stream.Collectors;

/**
 * WebRequestCostLogFilter
 * web端请求耗时日志过滤器
 *
 * @author hyuga
 * @date 2019-09-17 18:10
 */
@Component
public class WebRequestCostLogFilter extends OncePerRequestFilter {

    private static final String ACTUATOR = "actuator";

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!AgentWebRequestLogConfiguration.isCost()) {
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
        // requestTime
        long requestTime = System.currentTimeMillis();

        ContentCachingRequestWrapper wrapperRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrapperResponse = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(wrapperRequest, wrapperResponse);

        // responseTime
        long responseTime = System.currentTimeMillis();

        threadPoolSaveLog(request, requestTime, wrapperRequest, wrapperResponse, responseTime);
        wrapperResponse.copyBodyToResponse();
    }

    private void threadPoolSaveLog(HttpServletRequest request, long requestTime, ContentCachingRequestWrapper wrapperRequest, ContentCachingResponseWrapper wrapperResponse, long responseTime) {
        // 耗时阈值,单位：ms
        long requestCostTimeThresholdMs = AgentWebRequestLogConfiguration.costThresholdMs();
        // 请求耗时
        long costTime = responseTime - requestTime;
        String responseBody = AccessObjectUtil.getResponseBody(wrapperResponse);

        if (costTime < requestCostTimeThresholdMs) {
            return;
        }

        String method = request.getMethod().toUpperCase();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        String tpl = "\n耗时：{}  \nIP: {}  \nCODE: {}  \n开始计时: {}  \n计时结束：{}  \nURI: {}  \nMETHOD: [{}]  \nPARAMS: [{}] \n响应内容Size: {}  \n最大内存: {}m  已分配内存: {}m  已分配内存中的剩余空间: {}m  最大可用内存: {}m";
        String requestParams = AccessObjectUtil.getRequestParams(wrapperRequest);
        String requestBody = AccessObjectUtil.getRequestBody(wrapperRequest);

        String loggerMessage = StringUtil.format(tpl,
                costTime / 1000 + "s",
                CommonWebUtil.getIpAddr(request)[0],
                wrapperResponse.getStatusCode(),
                simpleDateFormat.format(requestTime),
                simpleDateFormat.format(responseTime),
                request.getRequestURI(),
                method,
                "GET".equalsIgnoreCase(method) ? StringUtil.removeAllSpace(requestParams) : StringUtil.removeAllSpace(requestBody),
                ByteUtil.convert(responseBody.getBytes().length, ByteType.B),
                Runtime.getRuntime().maxMemory() / 1024 / 1024,
                Runtime.getRuntime().totalMemory() / 1024 / 1024,
                Runtime.getRuntime().freeMemory() / 1024 / 1024,
                (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1024 / 1024);

        if (FeignConstants.isDeveloperLocalEnvironment(NetworkUtil.getLocalIpAddr())) {
            Console.redLog("接口耗时触发阈值：" + loggerMessage);
        } else {
            logger.info("接口耗时触发阈值：" + loggerMessage);
        }
    }

}
