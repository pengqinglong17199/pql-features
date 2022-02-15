package kfang.agent.feature.saas.request;

import kfang.infra.common.cache.cachekey.CacheKeyType;

/**
 * 缓存key
 *
 * @author pengqinglong
 * @since 2022/2/15
 */
public enum RequestCacheKey implements CacheKeyType {

    /**
     *
     */
    LIMIT;

    @Override
    public String getBaseType() {
        return "REQUEST";
    }

    @Override
    public String getCacheKey() {
        return this.name();
    }
}
