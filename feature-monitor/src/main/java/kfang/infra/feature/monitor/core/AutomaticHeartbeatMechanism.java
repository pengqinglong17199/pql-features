package kfang.infra.feature.monitor.core;

import kfang.infra.feature.monitor.core.processor.MonitorCoreProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 自动心跳机制
 *
 * @author 彭清龙
 * @date 2020-04-07 上午 9:13
 */
@Slf4j
@Component
@EnableScheduling
public class AutomaticHeartbeatMechanism implements ApplicationRunner, DisposableBean {

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        MonitorCoreProcessor.serviceUp();
    }

    /**
     * Heartbeat links are sent every minute.
     *
     * @throws Exception on error
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void heartbeat() throws Exception {
        MonitorCoreProcessor.heartbeat();
    }

    /**
     * Invoked by the containing {@code BeanFactory} on destruction of a bean.
     *
     * @throws Exception in case of shutdown errors. Exceptions will get logged
     *                   but not rethrown to allow other beans to release their resources as well.
     */
    @Override
    public void destroy() throws Exception {
        MonitorCoreProcessor.serviceDown();
    }

}
