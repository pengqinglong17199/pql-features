package kfang.agent.feature.saas.utils;

import cn.hyugatool.core.string.StringUtil;

/**
 * 敏感数据处理util
 *
 * @author pengqinglong
 * @since 2021/3/20
 */
public class SensitiveUtil {

    /**
     * 处理敏感数据 默认前后保留两位
     * 规则 #{[明文]}#&{[秘文]}&
     * 例子：#{[13312345678]}#&{[13xxxxxxx78]}&
     *
     * @param plaintext 明文
     * @return java.lang.String
     * @author 彭清龙
     * @since 4:07 下午 2021/3/20
     **/
    public static String handleSensitive(String plaintext) {
        return handleSensitive(plaintext, 2, 2);
    }

    /**
     * 处理敏感数据
     * <p>
     * 规则 #{[明文]}#&{[秘文]}&
     *
     * @param plaintext 明文
     * @param head      头保留多少位
     * @param tail      尾保留多少位
     * @return java.lang.String
     * @author 彭清龙
     * @since 4:20 下午 2021/3/20
     **/
    public static String handleSensitive(String plaintext, int head, int tail) {
        return handleSensitive(plaintext, head, tail, '*');
    }

    /**
     * 处理敏感数据
     * <p>
     * 规则 #{[明文]}#&{[秘文]}&
     *
     * @param plaintext 明文
     * @param head      头保留多少位
     * @param tail      尾保留多少位
     * @param mask      掩码
     * @return java.lang.String
     * @author 彭清龙
     * @since 4:20 下午 2021/3/20
     **/
    public static String handleSensitive(String plaintext, int head, int tail, char mask) {
        // 非空校验
        if (StringUtil.isEmpty(plaintext)) {
            return plaintext;
        }

        // length需要大于前后保留的位数
        int length = plaintext.length();
        if (length < (head + tail)) {
            return plaintext;
        }

        StringBuilder builder = new StringBuilder();
        // 明文处理
        builder.append("#{[");
        builder.append(plaintext);
        builder.append("]}#");

        // 密文处理
        builder.append("&{[");
        builder.append(plaintext, 0, head);

        int count = head;

        int end = length - tail;
        for (; count < end; count++) {
            builder.append(mask);
        }
        builder.append(plaintext, count, length);
        builder.append("]}&");

        return builder.toString();
    }

    /**
     * 明文直接加密为秘文 不拼接规则 默认头部保留3位尾部保留4位使用*作为掩码
     *
     * @param plaintext 明文
     * @return 秘文
     */
    public static String encodeText(String plaintext) {
        return encodeText(plaintext, 3, 4);
    }

    /**
     * 明文直接全部加密为秘文 不拼接规则
     *
     * @param plaintext 明文
     * @return str
     */
    public static String allEncodeText(String plaintext) {
        return encodeText(plaintext, 0, 0);
    }

    /**
     * 明文直接加密为秘文 不拼接规则 默认使用*作为掩码
     *
     * @param plaintext 明文
     * @param head      头部保留
     * @param tail      尾部保留
     * @return 秘文
     */
    public static String encodeText(String plaintext, int head, int tail) {

        return encodeText(plaintext, head, tail, '*');
    }

    /**
     * 明文直接加密为秘文 不拼接规则
     *
     * @param plaintext 明文
     * @param head      头部保留
     * @param tail      尾部保留
     * @param mask      掩码
     * @return 秘文
     */
    public static String encodeText(String plaintext, int head, int tail, char mask) {
        StringBuilder builder = new StringBuilder();

        // 非空校验
        if (StringUtil.isEmpty(plaintext)) {
            return plaintext;
        }

        // length需要大于前后保留的位数
        int length = plaintext.length();
        if (length < (head + tail)) {
            return plaintext;
        }

        int count = head;

        int end = length - tail;
        builder.append(plaintext, 0, head);
        for (; count < end; count++) {
            builder.append(mask);
        }
        builder.append(plaintext, count, length);

        return builder.toString();
    }

    /**
     * 明文直接加密为秘文 不拼接规则
     *
     * @param plaintext 明文
     * @param head      头部保留
     * @param tail      尾部保留
     * @param mask      掩码
     * @param maskSize  掩码数量 此字段大于0时 生效 此时tail字段失效
     * @return 秘文
     */
    public static String encodeText(String plaintext, int head, int tail, char mask, int maskSize) {
        if (maskSize > 0) {
            return encodeText(plaintext, head, mask, maskSize);
        } else {
            return encodeText(plaintext, head, tail, mask);
        }
    }

    /**
     * 明文直接加密为秘文 不拼接规则
     *
     * @param plaintext 明文
     * @param head      头部保留
     * @param mask      掩码
     * @param maskSize  掩码数量
     * @return 秘文
     */
    public static String encodeText(String plaintext, int head, char mask, int maskSize) {
        StringBuilder builder = new StringBuilder();

        // 非空校验
        if (StringUtil.isEmpty(plaintext)) {
            return plaintext;
        }

        int count = head;

        int end = maskSize + head;
        if (plaintext.length() < head) {
            builder.append(plaintext);
        } else {
            builder.append(plaintext, 0, head);

            for (; count < end && count < plaintext.length(); count++) {
                builder.append(mask);
            }
            builder.append(plaintext, count, plaintext.length());
        }

        return builder.toString();
    }

    /**
     * 获取密文
     *
     * @param ciphertext 密文
     * @return java.lang.String
     * @author 彭清龙
     * @since 5:17 下午 2021/3/20
     **/
    public static String acquireCiphertext(String ciphertext) {
        return acquireSensitive(ciphertext, false);
    }

    /**
     * 获取明文
     *
     * @param plaintext 密文
     * @return java.lang.String
     * @author 彭清龙
     * @since 5:18 下午 2021/3/20
     **/
    public static String acquirePlaintext(String plaintext) {
        return acquireSensitive(plaintext, true);
    }

    /**
     * @param ciphertext 密文
     * @return String
     * plaintextOrCiphertext 取明文还是取密文 true：取明文 false：取密文]
     * @author 彭清龙
     * @since 4:25 下午 2021/3/20
     */
    public static String acquireSensitive(String ciphertext, boolean plaintextOrCiphertext) {
        if (StringUtil.isEmpty(ciphertext)) {
            return ciphertext;
        }
        // 默认取密文
        String needStartMark = "&{[";
        String needEndMark = "]}&";

        String replaceStartMark = "#{[";
        String replaceEndMark = "]}#";

        // 取明文
        if (plaintextOrCiphertext) {
            needStartMark = "#{[";
            needEndMark = "]}#";

            replaceStartMark = "&{[";
            replaceEndMark = "]}&";
        }

        // 处理数据
        while (ciphertext.contains(replaceStartMark) && ciphertext.contains(replaceEndMark)) {
            int replaceStartIdx = ciphertext.indexOf(replaceStartMark);
            int replaceEndIdx = ciphertext.indexOf(replaceEndMark);

            // 获取需要的文本
            String replaceText = ciphertext.substring(replaceStartIdx, replaceEndIdx + 3);
            ciphertext = ciphertext.replace(needStartMark, "")
                    .replace(needEndMark, "")
                    .replace(replaceText, "");
        }
        return ciphertext;
    }

    public static void main(String[] args) {
        // 加密敏感信息 默认前后各保留两位
        String s1 = handleSensitive("13312345678");
        System.out.println("加密敏感信息 默认前后各保留两位---" + s1);

        // 加密敏感信息 前面保留三位 后面保留四位
        String s2 = handleSensitive("13312345678", 3, 4);
        System.out.println("加密敏感信息 前面保留三位 后面保留四位" + s2);

        // 获取明文密文
        String s3 = acquirePlaintext(s1);
        System.out.println("acquirePlaintext获取明文---" + s3);
        String s4 = acquireCiphertext(s1);
        System.out.println("acquireCiphertext获取密文---" + s4);

        // 获取明文
        String s5 = acquireSensitive(s1, true);
        System.out.println("acquireSensitive获取明文---" + s5);
        // 获取秘文
        String s6 = acquireSensitive(s1, false);
        System.out.println("acquireSensitive获取密文---" + s6);

        // 单加密
        System.out.println("单加密---" + SensitiveUtil.encodeText("13265116714"));
        // 单加密 前面保留三位 后面保留4位
        System.out.println("单加密 前面保留三位 后面保留4位---" + SensitiveUtil.encodeText("13265116714", 3, 4));

        // 使用#加密
        System.out.println("使用#加密---" + SensitiveUtil.encodeText("13265116714", 3, 4, '#'));
        System.out.println("使用¥加密---" + SensitiveUtil.handleSensitive("13265116714", 3, 4, '¥'));

        System.out.println("全部加密----" + SensitiveUtil.allEncodeText("1234567"));

        System.out.println("通过掩码数量进行加密 原数据 123 加密结果" + SensitiveUtil.encodeText("123", 3, '#', 5));
        System.out.println("通过掩码数量进行加密 原数据 123456 加密结果" + SensitiveUtil.encodeText("123456", 3, '#', 5));
        System.out.println("通过掩码数量进行加密 原数据 1234567890 加密结果" + SensitiveUtil.encodeText("1234567890", 3, '#', 5));
        System.out.println("通过掩码数量进行加密 原数据 1234567890abcdefg 加密结果" + SensitiveUtil.encodeText("1234567890abcdefg", 3, '#', 5));
    }

}