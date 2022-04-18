package kfang.agent.feature.saas.feign;

import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.core.uri.URLUtil;
import feign.Target;

import java.net.URL;

/**
 * FeignDaili
 *
 * @author pengqinglong
 * @since 2022/4/11
 */
public class FeignProxy<T> extends Target.HardCodedTarget<T> {

    private final HardCodedTarget<T> target;

    private final String suffix;

    public FeignProxy(HardCodedTarget<T> target, String suffix) {
        super(target.type(), target.url());
        this.target = target;
        this.suffix = suffix;
    }

    @Override
    public String url() {
        String urlStr = target.url();

        if (StringUtil.isEmpty(suffix)) {
            return urlStr;
        }

        if (StringUtil.hasText(urlStr)) {
            final URL url = URLUtil.url(urlStr);
            final String uri = URLUtil.getHost(url).toString();
            final String path = url.getPath();
            return String.format("%s-%s%s", uri, suffix, path);
        }
        return urlStr;
    }

}