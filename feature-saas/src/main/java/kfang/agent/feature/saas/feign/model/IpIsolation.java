package kfang.agent.feature.saas.feign.model;

import kfang.agent.feature.saas.feign.enums.EnvEnum;
import kfang.agent.feature.saas.feign.enums.ServiceIsolationEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * IP隔离
 *
 * @author hyuga
 * @since 2022-01-10 09:13:53
 */
@Data
@AllArgsConstructor
public class IpIsolation {

    private String ip;

    private EnvEnum env;

    private ServiceIsolationEnum isolation;

}
