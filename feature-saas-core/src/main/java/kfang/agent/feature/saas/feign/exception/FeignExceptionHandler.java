package kfang.agent.feature.saas.feign.exception;

import com.netflix.client.ClientException;
import feign.RetryableException;
import kfang.infra.api.JsonCommonCodeEnum;
import kfang.infra.common.KfangInfraCommonProperties;
import kfang.infra.web.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * FeignExceptionHandle
 *
 * @author hyuga
 * @since 2022/4/25
 */
@RestController
@ControllerAdvice
@Order(1)
@Slf4j
public class FeignExceptionHandler extends BaseController {

    private static final String SERVICE_AGENT = "service-agent-";

    @Resource
    private KfangInfraCommonProperties kfangInfraCommonProperties;

    /**
     * 客户端调用服务端超时异常处理
     * Read timed out executing POST http://service-agent-house-hyuga-iMac.local/houseRole/countRolePersonHouseByPersonId
     *
     * @param e e
     */
    @ExceptionHandler(value = RetryableException.class)
    public String retryableException(Exception e) throws Exception {
        log.error("web层调用service层超时异常 错误信息:" + e.getMessage(), e);

        String msg = e.getMessage();

        checkContainsAgentServiceSign(e, msg);
        String serviceName = msg.substring(msg.indexOf(SERVICE_AGENT));
        FeignCallExceptionConstants.ServiceType serviceType = FeignCallExceptionConstants.selectServiceByLike(serviceName);
        addAccessErrMsg(e.getMessage());

        String env = kfangInfraCommonProperties.getEnv().getDeploy();

        String tip = FeignCallExceptionConstants.appendReadTimeoutExceptionTip(env, serviceType);

        return withCustomMessage(tip, JsonCommonCodeEnum.E0005);
    }

    private void checkContainsAgentServiceSign(Exception e, String msg) throws Exception {
        if (!msg.contains(SERVICE_AGENT)) {
            throw e;
        }
    }

    /**
     * 客户端调用服务端中断异常处理
     * "com.netflix.client.ClientException: Load balancer does not have available server for client: service-agent-house-hyuga-iMac.local";
     *
     * @param e e
     * @return string
     * @throws Exception Exception
     */
    @ExceptionHandler(value = ClientException.class)
    public String clientException(Exception e) throws Exception {
        log.error("web层调用service层错误异常 错误信息:" + e.getMessage(), e);

        final String msg = e.getMessage();

        checkContainsAgentServiceSign(e, msg);
        String serviceName = msg.substring(msg.indexOf(SERVICE_AGENT));
        FeignCallExceptionConstants.ServiceType serviceType = FeignCallExceptionConstants.selectServiceByLike(serviceName);
        addAccessErrMsg(e.getMessage());

        String env = kfangInfraCommonProperties.getEnv().getDeploy();

        String tip = FeignCallExceptionConstants.appendClientExceptionTip(env, serviceType);

        return withCustomMessage(tip, JsonCommonCodeEnum.E0005);
    }

    public void addAccessErrMsg(String errMsg) {
        accessLogHandler.setProcessErrMsg(errMsg);
    }

}
