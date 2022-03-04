package kfang.agent.feature.saas.request.basicparam;

import cn.hyugatool.core.clazz.ClassUtil;
import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.collection.SetUtil;
import cn.hyugatool.core.date.DateUtil;
import cn.hyugatool.core.instance.ReflectionUtil;
import cn.hyugatool.core.object.MapperUtil;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.string.StringUtil;
import kfang.infra.api.validate.extend.LoginExtendDto;
import kfang.infra.api.validate.extend.OperateExtendForm;
import kfang.infra.api.validate.extend.PageExtendForm;
import kfang.infra.common.model.LoginDto;
import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.UserAgent;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;


/**
 * RequestBasicParamsProcessor
 *
 * @author hyuga
 * @since 2021/2/26
 */
public final class RequestBasicParamsProcessor {

    public static final String OPERATOR_IP = "operatorIp";
    public static final String OPERATOR_BROWSER = "operatorBrowser";
    public static final String OPERATOR_SYSTEM = "operatorSystem";
    public static final String OPERATOR_TIME = "operatorTime";
    public static final String LOGIN_EXTEND_DTO = "loginExtendDto";

    public static void setBasicParams(Object object, HttpServletRequest request, LoginDto loginDto, String system, String ip) {
        if (ObjectUtil.isNull(object)) {
            return;
        }

        final List<Field> declaredFields = ReflectionUtil.getDeclaredFields(object);

        for (Field field : ListUtil.optimize(declaredFields)) {
            // 获取字段值
            final Object fieldValue = ReflectionUtil.getFieldValue(object, field);

            String fieldName = field.getName();
            if (basicParamsSetValue(object, request, loginDto, system, ip, fieldName)) {
                continue;
            }
            Field declaredField = ReflectionUtil.getDeclaredField(object.getClass(), fieldName);
            Class<?> type = declaredField.getType();

            if (type == List.class) {
                if (ObjectUtil.nonNull(fieldValue)) {
                    List<Object> fieldValueObjectList = ObjectUtil.cast(fieldValue);
                    List<Object> optimize = ListUtil.optimize(fieldValueObjectList);
                    optimize.forEach(item -> setBasicParams(item, request, loginDto, system, ip));
                }
            } else if (ClassUtil.isNormalClass(type) && !ClassUtil.isBasicDataType(type)) {
                setBasicParams(fieldValue, request, loginDto, system, ip);
            }
        }
    }

    private static boolean basicParamsSetValue(Object object, HttpServletRequest request, LoginDto loginDto, String system, String ip, String field) {
        if (object instanceof OperateExtendForm || object instanceof PageExtendForm) {
            LoginExtendDto dto = MapperUtil.copy(loginDto, LoginExtendDto.class);
            if (dto.getMenuFunctionList() == null) {
                dto.setMenuFunctionList(SetUtil.newHashSet());
            }
            ReflectionUtil.setFieldValue(object, LOGIN_EXTEND_DTO, dto);
        }
        boolean operatorIpContains = containsThenSetValue(field, new String[]{OPERATOR_IP}, object, ip);
        if (operatorIpContains) {
            return true;
        }
        boolean operatorTimeContains = containsThenSetValue(field, new String[]{OPERATOR_TIME}, object, DateUtil.now());
        if (operatorTimeContains) {
            return true;
        }
        if (StringUtil.contains(field, new String[]{OPERATOR_BROWSER})) {
            //获取浏览器信息
            Browser browser = UserAgent.parseUserAgentString(request.getHeader("User-Agent")).getBrowser();
            ReflectionUtil.setFieldValue(object, field, browser.toString());
            return true;
        }
       /* if (StringUtil.contains(field, new String[]{OPERATOR_SYSTEM})) {
            String operatorSystem = OperatorSystemEnum.getOperatorSystemEnumName(system);
            if (StringUtil.isEmpty(system)) {
                throw new RuntimeException("操作来源不能为空");
            }
            ReflectionUtil.setFieldValue(object, OPERATOR_SYSTEM, operatorSystem);
            return true;
        }*/
        return false;
    }

    private static boolean containsThenSetValue(String field, String[] fieldArray, Object object, Object value) {
        boolean contains = false;
        if (StringUtil.contains(field, fieldArray)) {
            ReflectionUtil.setFieldValue(object, field, value);
            contains = true;
        }
        return contains;
    }
}