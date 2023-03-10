package kfang.agent.feature.saas.utils;

import cn.hyugatool.core.constants.HyugaConstants;
import cn.hyugatool.core.lang.Console;
import cn.hyugatool.core.lang.ConsoleAnsi;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.system.NetworkUtil;
import kfang.agent.feature.saas.constants.SaasConstants;
import kfang.infra.common.KfangInfraCommonProperties;
import kfang.infra.common.spring.SpringBeanPicker;
import org.fusesource.jansi.Ansi;
import org.springframework.core.env.Environment;

import javax.management.MalformedObjectNameException;

/**
 * QuickEntryUtil
 *
 * @author hyuga
 * @since 2021/5/28
 */
@SuppressWarnings("unused")
public final class QuickEntryUtil {

    private static final String SERVER_PORT = "server.port";

    @SuppressWarnings("all")
    public static final String HTTP = "http://";

    private static final String BUSINESS = "-business-";

    public static void print(String operatorSystem) throws MalformedObjectNameException {
        operatorSystem = operatorSystem.replaceAll(HyugaConstants.UNDERLINE, HyugaConstants.HYPHEN).toLowerCase();
        operatorSystem = operatorSystem.replaceAll(BUSINESS, HyugaConstants.HYPHEN).toLowerCase();
        String deploy = SpringBeanPicker.getBean(KfangInfraCommonProperties.class).getEnv().getDeploy();
        if (!SaasConstants.DEV.equals(deploy)) {
            return;
        }
        final String localIpAddr = NetworkUtil.getLocalIpAddr();

        Integer localPort = NetworkUtil.getLocalPort();
        if (ObjectUtil.isNull(localPort)) {
            Environment environment = SpringBeanPicker.getBean(Environment.class);
            if (ObjectUtil.nonNull(environment)) {
                String serverPort = environment.getProperty(SERVER_PORT);
                if (StringUtil.hasText(serverPort)) {
                    localPort = Integer.parseInt(serverPort);
                }
            }
        }

        final String separator = StringUtil.repeat("=", 200);

        Console.greenLog(separator);
        // swagger
        ConsoleAnsi.init()
                .color(Ansi.Color.YELLOW).append("SWAGGER:").color(Ansi.Color.BLUE).
                append(StringUtil.format(HTTP + "{}:{}/{}/doc.html", localIpAddr, localPort, operatorSystem)).print();
        // ????????????
        ConsoleAnsi.init()
                .color(Ansi.Color.YELLOW).append("KO TIME:").color(Ansi.Color.BLUE)
                .append(StringUtil.format(HTTP + "{}:{}/{}/koTime", localIpAddr, localPort, operatorSystem)).print();
        Console.greenLog(separator);
    }

}
