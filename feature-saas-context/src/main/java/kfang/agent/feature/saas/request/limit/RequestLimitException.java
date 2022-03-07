package kfang.agent.feature.saas.request.limit;

import kfang.infra.api.RequestResultException;

/**
 * RequestLimitException
 *
 * @author pengqinglong
 * @since 2022/2/15
 */
public class RequestLimitException extends RequestResultException {

    public RequestLimitException(String message) {
        super(message);
    }

    protected RequestLimitException(String message, String code) {
        super(message, code);
    }

}