package kfang.agent.feature.saas.request.logger;

import kfang.agent.feature.saas.request.logger.filter.RequestCostLogFilter;
import kfang.agent.feature.saas.request.logger.filter.WebRequestLogFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * 盘客feign核心类
 *
 * @author pengqinglong
 * @since 2021/12/29
 */
@Component
@Slf4j
@Import({WebRequestLogFilter.class, RequestCostLogFilter.class})
public class AgentRequestLogCore implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("AgentRequestLog start success~");
    }

}