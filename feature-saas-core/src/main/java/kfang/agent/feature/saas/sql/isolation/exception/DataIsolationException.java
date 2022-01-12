package kfang.agent.feature.saas.sql.isolation.exception;

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