package kfang.agent.feature.saas.thirdparty.delegate;

import kfang.agent.feature.saas.thirdparty.entity.ThirdpartyForm;

/**
 * 表单参数方式请求（暂未支持）
 *
 * @author pengqinglong
 * @since 2022/3/15
 */
public class ParamRequestDelegate implements RequestDelegate {

    @Override
    public String post(String url, ThirdpartyForm form) {
        return null;
    }

    @Override
    public String get(String url, ThirdpartyForm form) {
        return null;
    }

}