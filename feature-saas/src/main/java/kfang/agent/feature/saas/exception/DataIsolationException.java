package kfang.agent.feature.saas.exception;

/**
 * 数据隔离异常
 *
 * @author pengqinglong
 * @since 2021/12/15
 */
public class DataIsolationException extends RuntimeException{

    public DataIsolationException(String message) {
        super(message);
    }

}