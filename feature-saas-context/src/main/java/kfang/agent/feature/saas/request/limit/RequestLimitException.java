package kfang.agent.feature.saas.request.limit;

/**
 * RequestLimitException
 *
 * @author pengqinglong
 * @since 2022/2/15
 */
public class RequestLimitException extends RuntimeException{

    public RequestLimitException(String s) {
        super(s);
    }
}