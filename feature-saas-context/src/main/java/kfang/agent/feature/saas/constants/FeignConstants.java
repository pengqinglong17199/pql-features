package kfang.agent.feature.saas.constants;

import cn.hyugatool.core.collection.ArrayUtil;
import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.string.StringUtil;
import kfang.agent.feature.saas.feign.enums.EnvEnum;
import kfang.agent.feature.saas.feign.enums.ServiceIsolationEnum;
import kfang.agent.feature.saas.feign.model.IpIsolation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hyuga
 * @since 2022-01-07 18:45:12
 */
public interface FeignConstants {

    String FEIGN_SUFFIX = "feign.suffix";

    String DYNAMIC_SERVICE_NAME_PROPERTIES = "agent-feign.properties";
    String DEFAULT_ENV_IP_SUFFIX = "default-env-ip-suffix";

    /**
     * IP是否需要进行隔离
     *
     * @param localIpAddr 本地服务IP
     * @return boolean
     */
    static boolean ipWhetherNeedIsolation(String localIpAddr) {
        IpIsolation ipIsolation = IP_ISOLATION_LIST.stream().filter(ipModel -> StringUtil.equals(ipModel.getIp(), localIpAddr)).collect(Collectors.toList()).stream().findFirst().orElse(null);
        if (ObjectUtil.isNull(ipIsolation)) {
            throw new RuntimeException("当前服务IP不允许启动该服务，请联系管理员! ip:" + localIpAddr);
        }
        return StringUtil.equals(ipIsolation.getIsolation(), ServiceIsolationEnum.ISOLATION);
    }

    /**
     * 是否测试环境
     *
     * @param localIpAddr 本地服务IP
     * @return boolean
     */
    static boolean isTestEnvironment(String localIpAddr) {
        IpIsolation ipIsolation = IP_ISOLATION_LIST.stream().filter(ipModel -> StringUtil.equals(ipModel.getIp(), localIpAddr)).collect(Collectors.toList()).stream().findFirst().orElse(null);
        return ObjectUtil.nonNull(ipIsolation) && StringUtil.equals(ipIsolation.getEnv(), EnvEnum.TEST_ENV);
    }

    /**
     * 是否开发环境
     *
     * @param localIpAddr 本地服务IP
     * @return boolean
     */
    static boolean isDeveloperLocalEnvironment(String localIpAddr) {
        IpIsolation ipIsolation = IP_ISOLATION_LIST.stream().filter(ipModel -> StringUtil.equals(ipModel.getIp(), localIpAddr)).collect(Collectors.toList()).stream().findFirst().orElse(null);
        return ObjectUtil.nonNull(ipIsolation) && StringUtil.equals(ipIsolation.getEnv(), EnvEnum.DEV_ENV);
    }

    /**
     * 获取测试环境ip后缀
     *
     * @param localIpAddr 本地服务IP
     * @return ip后缀
     */
    static String getSuffixOfTest(String localIpAddr) {
        IpIsolation ipIsolation = IP_ISOLATION_LIST.stream().filter(ipModel -> StringUtil.equals(ipModel.getIp(), localIpAddr)).collect(Collectors.toList()).stream().findFirst().orElse(null);
        if (ObjectUtil.isNull(ipIsolation)) {
            throw new RuntimeException(String.format("当前环境未支持:%s", localIpAddr));
        }
        if (ObjectUtil.equals(ipIsolation.getIsolation(), ServiceIsolationEnum.DEFAULT)) {
            return StringUtil.EMPTY;
        }
        return ListUtil.findLast(ArrayUtil.asList(localIpAddr.split("\\.")));
    }

    List<IpIsolation> IP_ISOLATION_LIST = new ArrayList<>() {{
        // 测试环境IP
        add(new IpIsolation("10.210.10.54", EnvEnum.TEST_ENV, ServiceIsolationEnum.DEFAULT));// 默认测试环境
        add(new IpIsolation("10.210.10.60", EnvEnum.TEST_ENV, ServiceIsolationEnum.ISOLATION));// 60测试环境
        add(new IpIsolation("10.210.10.63", EnvEnum.TEST_ENV, ServiceIsolationEnum.ISOLATION));// 63测试环境
        add(new IpIsolation("10.210.10.85", EnvEnum.TEST_ENV, ServiceIsolationEnum.ISOLATION));// 85测试环境
        // 开发本地IP
        add(new IpIsolation("10.210.13.13", EnvEnum.DEV_ENV, ServiceIsolationEnum.ISOLATION));// huangzeyuan
        add(new IpIsolation("10.210.200.200", EnvEnum.DEV_ENV, ServiceIsolationEnum.ISOLATION));// huangzeyuan
        add(new IpIsolation("10.210.13.66", EnvEnum.DEV_ENV, ServiceIsolationEnum.ISOLATION));// pengqinglong
        add(new IpIsolation("10.210.13.8", EnvEnum.DEV_ENV, ServiceIsolationEnum.ISOLATION));// chenyi
        add(new IpIsolation("10.210.13.9", EnvEnum.DEV_ENV, ServiceIsolationEnum.ISOLATION));// liuwei
        add(new IpIsolation("10.210.13.77", EnvEnum.DEV_ENV, ServiceIsolationEnum.ISOLATION));// zhongyuan
        add(new IpIsolation("10.210.13.231", EnvEnum.DEV_ENV, ServiceIsolationEnum.ISOLATION));//chenhao
        add(new IpIsolation("10.210.13.32", EnvEnum.DEV_ENV, ServiceIsolationEnum.ISOLATION));// fanwei
        add(new IpIsolation("10.210.13.37", EnvEnum.DEV_ENV, ServiceIsolationEnum.ISOLATION));// zhangchenglong
    }};

}
