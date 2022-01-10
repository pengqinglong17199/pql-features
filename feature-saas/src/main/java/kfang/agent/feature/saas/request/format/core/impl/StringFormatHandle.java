package kfang.agent.feature.saas.request.format.core.impl;

import cn.hyugatool.extra.emoji.EmojiUtil;
import kfang.agent.feature.saas.request.format.anntations.StringFormat;
import kfang.agent.feature.saas.request.format.core.FormatHandle;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 去除参数空格处理器
 *
 * @author pengqinglong
 * @since 2021/11/15
 */
@Slf4j
public class StringFormatHandle implements FormatHandle {

    public static final int START = 0xff;
    public static final int END = 0xff;
    public static final char SPACE = ' ';

    @Override
    public void handle(Annotation annotation, Field field, Object obj) throws Exception {

        field.setAccessible(true);
        Object object = field.get(obj);

        if (object == null) {
            return;
        }

        if (!(object instanceof String)) {
            throw new Exception(field.getDeclaringClass().getName() + "." + field.getName() + "非string类型 请检查代码");
        }

        try {
            String str = object.toString();
            StringFormat stringFormat = (StringFormat) annotation;

            int st = 0;
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            int len = bytes.length;

            // 处理开头 详情请看String.trim源码
            if (stringFormat.front()) {
                while ((st < len) && ((bytes[st] & START) <= SPACE)) {
                    st++;
                }
            }

            // 处理结尾 详情请看String.trim源码
            if (stringFormat.rear()) {
                while ((st < len) && ((bytes[len - 1] & END) <= SPACE)) {
                    len--;
                }
            }

            String newValue = new String(Arrays.copyOfRange(bytes, st, len), StandardCharsets.UTF_8);

            // 处理表情符
            if (stringFormat.emoji()) {
                newValue = EmojiUtil.removeAllEmojis(newValue);
            }

            field.set(obj, newValue);
        } catch (Exception e) {
            log.error("请求参数格式化-StringFormatHandle处理异常", e);
        }
    }
}