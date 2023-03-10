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
        HOUSE("??????"),
        HOUSE_ELASTICSEARCH("??????????????????"),
        HOUSE_PRE("???????????????"),
        CUSTOMER("??????"),
        CUSTOMER_ELASTICSEARCH("??????????????????"),
        SURVEY("??????"),
        COMPLAINT("????????????"),
        OMS("??????"),
        THIRDPARTY("?????????"),
        REPORT("??????"),
        DTS("DTS"),
        OTHER("?????????"),

        INFRA("??????"),
        NEWHOUSE("??????"),
        ONLINE("??????"),
        TRADE("??????"),
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
            return String.format("%s??????????????????????????????????????????", serviceType.getDesc());
        }
        return JsonCommonCodeEnum.E0018.getMessage();
    }

    public static String appendClientExceptionTip(String env, ServiceType serviceType) {
        if (SaasConstants.DEV.equals(env)) {
            return String.format("%s??????????????????????????????????????????", serviceType.getDesc());
        }
        return JsonCommonCodeEnum.E0005.getMessage();
    }

}
