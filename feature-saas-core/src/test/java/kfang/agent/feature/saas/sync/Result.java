package kfang.agent.feature.saas.sync;

/**
 * 返回结果
 */
public interface Result{

    /**
     * 主键
     */
    String getPrimaryKey();

    /**
     * 计算结果
     */
    Object getResult();
}