package cn.hyuga.feature.wechat.utils;

import cn.hyugatool.core.date.DateFormat;
import cn.hyugatool.core.date.DateUtil;

import java.util.Date;

/**
 * DateFormatUtil
 *
 * @author hyuga
 * @date 2020-08-25 上午10:11
 */
public class DateFormatUtil {

    /**
     * 转换成服务号日期格式
     *
     * @return 12月12日 12时12分
     */
    public static String toServiceNoDateFormat(Date date) {
        String format = DateUtil.format(DateFormat.yyyy_MM_dd_HH_mm, date);
        String[] dateArray = format.split(" ");
        String[] dateSplits = dateArray[0].split("-");
        String[] timeSplits = dateArray[1].split(":");
        return String.format("%s月%s日 %s时 %s分",
                Integer.valueOf(dateSplits[1]),
                Integer.valueOf(dateSplits[2]),
                Integer.valueOf(timeSplits[0]),
                Integer.valueOf(timeSplits[1]));
    }

}
