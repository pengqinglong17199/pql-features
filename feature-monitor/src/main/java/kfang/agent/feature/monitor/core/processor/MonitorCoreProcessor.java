package kfang.agent.feature.monitor.core.processor;

import cn.hyugatool.core.date.DateUtil;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.string.snow.SnowflakeIdUtil;
import cn.hyugatool.json.JsonUtil;
import cn.hyugatool.system.NetworkUtil;
import kfang.agent.feature.monitor.MonitorConfiguration;
import kfang.agent.feature.monitor.constants.MonitorConstant;
import kfang.agent.feature.monitor.constants.ServiceHeartbeatKey;
import kfang.agent.feature.monitor.model.ServiceMessage;
import kfang.infra.common.KfangInfraCommonProperties;
import kfang.infra.common.spring.SpringBeanPicker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.env.Environment;

/**
 * 服务监控
 *
 * @author hyuga
 * @since 2021-11-26 10:18:11
 */
@Slf4j
public class MonitorCoreProcessor {

    /**
     * Kfang基础公共属性
     */
    private static final KfangInfraCommonProperties KFANG_INFRA_COMMON_PROPERTIES = SpringBeanPicker.getBean(KfangInfraCommonProperties.class);
    /**
     * 服务IP
     */
    private static final String IP = NetworkUtil.getLocalIpAddr();
    /**
     * 服务端口
     */
    private static final int PORT = Integer.parseInt(SpringBeanPicker.getBean(Environment.class).getProperty(MonitorConstant.SERVER_PORT, "0"));
    /**
     * 服务名
     */
    private static final String SERVICE_NAME = KFANG_INFRA_COMMON_PROPERTIES.getEnv().getAppName();
    /**
     * 服务环境
     */
    private static final String DEPLOY = KFANG_INFRA_COMMON_PROPERTIES.getEnv().getDeploy().toUpperCase();
    /**
     * RabbitMQ
     */
    private static final RabbitTemplate RABBIT_TEMPLATE = SpringBeanPicker.getBean(RabbitTemplate.class);

    public static void serviceUp() {
        ServiceMessage message = new ServiceMessage();
        message.setProject(MonitorConfiguration.project());
        message.setIp(IP);
        message.setPort(PORT);
        message.setName(SERVICE_NAME);
        message.setEnvironment(DEPLOY);
        up(message);

        log.info("monitor client start success~");
    }

    public static void serviceDown() {
        ServiceMessage message = new ServiceMessage();
        message.setProject(MonitorConfiguration.project());
        message.setIp(MonitorCoreProcessor.IP);
        message.setPort(MonitorCoreProcessor.PORT);
        message.setName(MonitorCoreProcessor.SERVICE_NAME);
        message.setEnvironment(DEPLOY);
        down(message);

        log.info("monitor client destroy success~");
    }

    public static void heartbeat() {
        ServiceMessage message = new ServiceMessage();
        message.setProject(MonitorConfiguration.project());
        message.setIp(MonitorCoreProcessor.IP);
        message.setPort(MonitorCoreProcessor.PORT);
        message.setName(MonitorCoreProcessor.SERVICE_NAME);
        message.setEnvironment(DEPLOY);
        heartbeat(message);

        log.info("monitor client heartbeat success~");
    }

    private static Message getMessage(ServiceMessage serviceMessage) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setMessageId(SnowflakeIdUtil.getSnowflakeIdStr());
        messageProperties.setTimestamp(DateUtil.now());
        return new Message(JsonUtil.toJsonString(serviceMessage).getBytes(), messageProperties);
    }

    private static void up(ServiceMessage serviceMessage) {
        send(ServiceHeartbeatKey.SERVICE_UP_QUEUE, serviceMessage);
    }

    private static void heartbeat(ServiceMessage serviceMessage) {
        send(ServiceHeartbeatKey.SERVICE_HEARTBEAT_QUEUE, serviceMessage);
    }

    private static void down(ServiceMessage serviceMessage) {
        send(ServiceHeartbeatKey.SERVICE_DOWN_QUEUE, serviceMessage);
    }

    private static void send(String serviceHeartbeatKey, ServiceMessage serviceMessage) {
        Integer port = serviceMessage.getPort();
        boolean isSingleTest = ObjectUtil.equals(port, "-1");
        if (isSingleTest) {
            return;
        }
        RABBIT_TEMPLATE.convertAndSend(serviceHeartbeatKey, getMessage(serviceMessage));
    }

}
