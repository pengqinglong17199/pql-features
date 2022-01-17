package kfang.agent.feature.saas.utils;

import cn.hyugatool.core.lang.Console;
import cn.hyugatool.core.lang.ConsoleAnsi;
import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.system.NetworkUtil;
import kfang.agent.feature.saas.constants.SaasConstants;
import kfang.infra.common.KfangInfraCommonProperties;
import kfang.infra.common.spring.SpringBeanPicker;
import org.fusesource.jansi.Ansi;

import javax.management.MalformedObjectNameException;

/**
 * QuickEntryUtil
 *
 * @author hyuga
 * @since 2021/5/28
 */
public final class QuickEntryUtil {

    public static void print(String operatorSystem) throws MalformedObjectNameException {
        String deploy = SpringBeanPicker.getBean(KfangInfraCommonProperties.class).getEnv().getDeploy();
        if (!SaasConstants.DEV.equals(deploy)) {
            return;
        }
        final String localIpAddr = NetworkUtil.getLocalIpAddr();
        final int localPort = NetworkUtil.getLocalPort();
        final StringBuilder separator = StringUtil.repeatedlyAdded("=", 200);

        Console.greenLog(separator);
        // swagger
        ConsoleAnsi.init()
                .color(Ansi.Color.YELLOW).append("SWAGGER:").color(Ansi.Color.BLUE).
                append(StringUtil.format("http://{}:{}/{}/doc.html", localIpAddr, localPort, operatorSystem)).print();
        // 链路追踪
        ConsoleAnsi.init()
                .color(Ansi.Color.YELLOW).append("KO TIME:").color(Ansi.Color.BLUE)
                .append(StringUtil.format("http://{}:{}/{}/koTime", localIpAddr, localPort, operatorSystem)).print();
        Console.greenLog(separator);
    }

}
