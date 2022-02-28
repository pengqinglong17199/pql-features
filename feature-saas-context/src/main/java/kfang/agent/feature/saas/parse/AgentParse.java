package kfang.agent.feature.saas.parse;

/**
 * 盘客基础解析器
 *
 * @author pengqinglong
 * @since 2022/2/15
 */
public interface AgentParse<T, V> {

    /**
     * 将对象解析后返回
     *
     * @param t t
     * @return v
     */
    V parse(T t);

}