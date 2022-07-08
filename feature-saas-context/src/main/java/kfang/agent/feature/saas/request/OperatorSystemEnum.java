package kfang.agent.feature.saas.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * OperatorSystemEnum
 *
 * @author hyuga
 * @since 2021/3/13
 */
@AllArgsConstructor
public enum OperatorSystemEnum {

    /**
     *
     */
    WEB_AGENT_BUSINESS_PC( "PC", "业务系统PC"),
    WEB_AGENT_BUSINESS_APP( "APP", "业务系统APP"),
    WEB_AGENT_BUSINESS_APP_IOS( "APP_IOS", "IOS"),
    WEB_AGENT_BUSINESS_APP_ANDROID( "APP_ANDROID", "安卓"),
    WEB_AGENT_OMS( "OMS", "运营系统PC"),
    WEB_AGENT_APPLET("APPLET", "小程序"),
    WEB_AGENT_THIRDPARTY("THIRD_PARTY", "第三方"),
    SERVICE_TRADE("TRADE", "交易项目组"),
    SERVICE_SCHEDULER("SCHEDULER", "调度系统"),
    SERVICE_DTS("DTS", "DTS服务"),
    ;

    @Getter
    private final String desc;
    @Getter
    private final String description;

    public static String getOperatorSystemEnumName(String applicationName) {
        OperatorSystemEnum operatorSystemEnum = Arrays.stream(OperatorSystemEnum.values())
                .filter(item -> item.name().equals(applicationName)).findFirst().orElse(null);
        if (null != operatorSystemEnum) {
            return operatorSystemEnum.name();
        }
        return null;
    }

    public static boolean isBusinessPc(String applicationName) {
        return WEB_AGENT_BUSINESS_PC.name().equals(applicationName);
    }

    public static boolean isBusinessApp(String applicationName) {
        return WEB_AGENT_BUSINESS_APP.name().equals(applicationName);
    }

    public static boolean isBusinessApplet(String applicationName) {
        return WEB_AGENT_APPLET.name().equals(applicationName);
    }

    public static boolean isOms(String applicationName) {
        return WEB_AGENT_OMS.name().equals(applicationName);
    }

    public static boolean isScheduler(String applicationName) {
        return SERVICE_SCHEDULER.name().equals(applicationName);
    }

    public static boolean isThirdparty(String applicationName) {
        return WEB_AGENT_THIRDPARTY.name().equals(applicationName);
    }

    public static boolean isDts(String applicationName) {
        return SERVICE_DTS.name().equals(applicationName);
    }

    // public static OperatorSystemEnum parseApiPrefix(String requestUri) {
    //     final String requestUriPrefix = parseRequestUrlApiPrefix(requestUri);
    //     for (OperatorSystemEnum operatorSystemEnum : OperatorSystemEnum.values()) {
    //         if (requestUriPrefix.equalsIgnoreCase(operatorSystemEnum.getApplicationName())) {
    //             return operatorSystemEnum;
    //         }
    //     }
    //     return null;
    // }

    // private static String parseRequestUrlApiPrefix(String requestUrl) {
    //     return requestUrl.split("/")[1];
    // }

}
