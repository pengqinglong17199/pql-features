package kfang.agent.feature.saas.feign;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.core.uri.URLUtil;
import feign.Target;
import org.w3c.dom.events.Event;

import java.net.URL;
import java.util.List;

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

        if (AgentEnvironmentEnum.AGENT_UAT_DEFAULT.getSuffix().equals(suffix)) {
            final URL url = URLUtil.url(urlStr);
            final String path = url.getPath();
            return String.format("http://%s%s", target.name(), path);
        }

        if (StringUtil.hasText(urlStr)) {
            final URL url = URLUtil.url(urlStr);
            final String path = url.getPath();
            return String.format("http://%s-%s%s", target.name(), suffix, path);
        }
        return urlStr;
    }

}