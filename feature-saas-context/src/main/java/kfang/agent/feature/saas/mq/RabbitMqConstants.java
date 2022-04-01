package kfang.agent.feature.saas.mq;

/**
 * 说明：
 *
 * @author hyuga
 * @since 2019-11-20
 */
public interface RabbitMqConstants {

    /**
     * MQ服务名后缀配置
     */
    String SERVER_NAME_SUFFIX_CONFIG = "#{rabbitConfig.get('serverName')}";
    /**
     * MQ服务自动删除配置
     */
    String SERVER_AUTO_DELETE_CONFIG = "#{rabbitConfig.get('autoDelete')}";

    String SINGLE_LISTENER_CONTAINER = "singleListenerContainer";

    String PRE_JMS_MAIN_SCHEDULE = "preJmsMainSchedule";

}
