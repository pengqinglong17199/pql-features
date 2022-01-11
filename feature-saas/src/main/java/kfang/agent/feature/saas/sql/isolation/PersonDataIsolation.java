package kfang.agent.feature.saas.sql.isolation;

/**
 * 人员级别数据隔离接口
 *
 * @author pengqinglong
 * @since 2022/1/11
 */
public interface PersonDataIsolation {

    /**
     * 获取人员id
     * @return
     */
    String getPersonId();
}