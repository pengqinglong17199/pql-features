package kfang.agent.feature.saas.thirdparty.factory;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.collection.MapUtil;
import cn.hyugatool.core.object.ObjectUtil;
import kfang.agent.feature.saas.thirdparty.authentication.ThirdpartyAuthentication;
import kfang.agent.feature.saas.thirdparty.config.ThirdpartyConfig;
import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;
import kfang.infra.common.spring.SpringBeanPicker;
import org.springframework.context.support.AbstractApplicationContext;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 第三方请求鉴权器工厂类
 *
 * @author pengqinglong
 * @since 2022/3/15
 */
public class ThirdpartyAuthenticationFactory {

    /**
     * 配置映射
     */
    private static final Map<Class<? extends ThirdpartyForm>, ThirdpartyAuthentication<ThirdpartyConfig, ThirdpartyForm>> CONFIG_MAPPING;

    static{
        /*
         * 初始化配置映射
         */
        CONFIG_MAPPING = MapUtil.newHashMap();
        AbstractApplicationContext applicationContext = SpringBeanPicker.getApplicationContext();
        String[] beanNamesForType = applicationContext.getBeanNamesForType(ThirdpartyAuthentication.class);
        for (String beanName : ListUtil.optimize(beanNamesForType)) {
            Object bean = applicationContext.getBean(beanName);
            if (!(bean instanceof ThirdpartyAuthentication)) {
                continue;
            }
            ThirdpartyAuthentication<ThirdpartyConfig, ThirdpartyForm> authentication = ObjectUtil.cast(bean);
            Class<? extends ThirdpartyForm> authenticationClass = authentication.getAuthenticationClass();

            CONFIG_MAPPING.put(authenticationClass, authentication);
        }
    }

    /**
     * 通过鉴权对象获取第三方配置信息
     */
    public static ThirdpartyAuthentication<ThirdpartyConfig, ThirdpartyForm> getAuthentication(ThirdpartyForm form) {
        ThirdpartyAuthentication<ThirdpartyConfig, ThirdpartyForm> authentication = CONFIG_MAPPING.get(form.getClass());
        if (authentication == null) {
            Type genericSuperclass = authentication.getClass().getGenericSuperclass();
            try {
                authentication = CONFIG_MAPPING.get(Class.forName(genericSuperclass.getTypeName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return authentication;
    }

}