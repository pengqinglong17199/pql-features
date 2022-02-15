package kfang.agent.feature.saas.parse.time;

import kfang.agent.feature.saas.parse.exception.ParseException;
import kfang.agent.feature.saas.parse.time.constant.LimitTime;
import kfang.agent.feature.saas.parse.time.core.ChineseTimeParse;
import kfang.agent.feature.saas.parse.time.core.EnglishTimeParse;
import kfang.agent.feature.saas.parse.time.core.TimeParse;

import java.util.Objects;

/**
 * 时间解析器工厂
 *
 * @author pengqinglong
 * @since 2022/2/15
 */
public class TimeParseFactory {

    private static final String[] CHINESE = {"年", "月", "日", "时", "分", "秒"};

    private static final String[] ENGLISH = {"y", "M", "d", "h", "m", "s"};

    public static TimeParse create(String time){
        if (Objects.equals(LimitTime.TO_DAY, time)
        || Objects.equals(LimitTime.TO_DAY_NOW, time)) {
            return new ChineseTimeParse();
        }

        for (String temp : CHINESE) {
            if(time.contains(temp)){
                return new ChineseTimeParse();
            }
        }

        for (String temp : ENGLISH) {
            if(time.contains(temp)){
                return new EnglishTimeParse();
            }
        }

        throw new ParseException("[%s] 无法找到匹配的parse");
    }
}