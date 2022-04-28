package kfang.agent.feature.saas.feign.exception;

import cn.hyugatool.core.collection.MapUtil;
import kfang.agent.feature.saas.constants.SaasConstants;
import kfang.infra.api.JsonCommonCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * FeignEnum
 *
 * @author hyuga
 * @since 2022/4/25
 */
public class FeignCallExceptionConstants {

    public static final Map<String, ServiceType> SERVICE_STATEMENT = MapUtil.newHashMap();

    static {
        SERVICE_STATEMENT.put("service-agent-house", ServiceType.HOUSE);
        SERVICE_STATEMENT.put("service-agent-house-es", ServiceType.HOUSE_ELASTICSEARCH);
        SERVICE_STATEMENT.put("service-agent-house-pre", ServiceType.HOUSE_PRE);
        SERVICE_STATEMENT.put("service-agent-customer", ServiceType.HOUSE);
        SERVICE_STATEMENT.put("service-agent-customer-es", ServiceType.CUSTOMER_ELASTICSEARCH);
        SERVICE_STATEMENT.put("service-agent-complaint", ServiceType.COMPLAINT);
        SERVICE_STATEMENT.put("service-agent-dts", ServiceType.DTS);
        // SERVICE_STATEMENT.put("service-agent-jms", ServiceType.JMS);
        SERVICE_STATEMENT.put("service-agent-oms", ServiceType.OMS);
        // SERVICE_STATEMENT.put("service-agent-open", ServiceType.OPEN);
        SERVICE_STATEMENT.put("service-agent-thirdparty", ServiceType.THIRDPARTY);
        SERVICE_STATEMENT.put("service-agent-survey", ServiceType.SURVEY);
        SERVICE_STATEMENT.put("service-agent-report", ServiceType.REPORT);
        SERVICE_STATEMENT.put("other", ServiceType.OTHER);

        SERVICE_STATEMENT.put("service-infra-", ServiceType.INFRA);
        SERVICE_STATEMENT.put("service-newhouse-", ServiceType.NEWHOUSE);
        SERVICE_STATEMENT.put("service-online-", ServiceType.ONLINE);
        SERVICE_STATEMENT.put("service-trade-", ServiceType.TRADE);
    }

    @Getter
    @AllArgsConstructor
    public enum ServiceType {
        /**
         *
         */
        HOUSE("房源"),
        HOUSE_ELASTICSEARCH("房源搜索引擎"),
        HOUSE_PRE("房源预录入"),
        CUSTOMER("房源"),
        CUSTOMER_ELASTICSEARCH("房源搜索引擎"),
        SURVEY("实勘"),
        COMPLAINT("投诉举报"),
        OMS("运营"),
        THIRDPARTY("第三方"),
        REPORT("报表"),
        DTS("DTS"),
        OTHER("第三方"),

        INFRA("基础"),
        NEWHOUSE("新房"),
        ONLINE("外网"),
        TRADE("交易"),
        ;

        private final String desc;
    }

    public static ServiceType selectServiceByLike(String serviceName) {
        Set<String> serviceNameSetOrderByNameLength = SERVICE_STATEMENT.keySet().stream()
                .sorted(Comparator.comparingInt(String::length))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (String service : serviceNameSetOrderByNameLength) {
            if (serviceName.startsWith(service)) {
                return SERVICE_STATEMENT.get(service);
            }
        }
        return ServiceType.OTHER;
    }

    public static String appendReadTimeoutExceptionTip(String env, ServiceType serviceType) {
        if (SaasConstants.DEV.equals(env)) {
            return String.format("%s模块服务请求超时，请稍后再试", serviceType.getDesc());
        }
        return JsonCommonCodeEnum.E0018.getMessage();
    }

    public static String appendClientExceptionTip(String env, ServiceType serviceType) {
        if (SaasConstants.DEV.equals(env)) {
            return String.format("%s模块服务请求异常，请稍后再试", serviceType.getDesc());
        }
        return JsonCommonCodeEnum.E0005.getMessage();
    }

}
