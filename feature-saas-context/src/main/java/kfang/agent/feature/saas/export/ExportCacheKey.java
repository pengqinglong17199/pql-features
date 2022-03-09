package kfang.agent.feature.saas.export;

import kfang.infra.common.cache.cachekey.CacheKeyType;

/**
 * 缓存key
 *
 * @author pengqinglong
 * @since 2022/2/15
 */
public enum ExportCacheKey implements CacheKeyType {

    /**
     *
     */
    LIMIT;

    @Override
    public String getBaseType() {
        return "EXPORT";
    }

    @Override
    public String getCacheKey() {
        return this.name();
    }
}
