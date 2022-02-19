package kfang.agent.feature.saas.parse.time;

import cn.hyugatool.core.string.StringUtil;
import kfang.agent.feature.saas.parse.exception.ParseException;
import kfang.agent.feature.saas.parse.time.constant.LimitTime;
import kfang.agent.feature.saas.parse.time.core.ChineseTimeParse;
import kfang.agent.feature.saas.parse.time.core.EnglishTimeParse;
import kfang.agent.feature.saas.parse.time.core.TimeParse;

import java.util.Arrays;

/**
 * 时间解析器工厂
 *
 * @author pengqinglong
 * @since 2022/2/15
 */
public class TimeParseFactory {

    private static final String[] CHINESE = {"年", "月", "日", "时", "分", "秒"};

    private static final String[] ENGLISH = {"y", "M", "d", "h", "m", "s"};

    public static TimeParse create(String time) {
        boolean isChineseType = StringUtil.contains(time, new String[]{LimitTime.TO_DAY, LimitTime.TO_DAY_NOW});
        if (isChineseType) {
            return new ChineseTimeParse();
        }

        boolean containChinese = Arrays.stream(CHINESE).anyMatch(time::contains);
        if (containChinese) {
            return new ChineseTimeParse();
        }

        boolean containEnglish = Arrays.stream(ENGLISH).anyMatch(time::contains);
        if (containEnglish) {
            return new EnglishTimeParse();
        }

        throw new ParseException("[%s] 无法找到匹配的parse");
    }

    public static void main(String[] args) {
        String time = "1分50秒";
        TimeParse timeParse = create(time);
        Long parse = timeParse.parse(time);
        System.out.println(parse);
    }

}