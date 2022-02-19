package kfang.agent.feature.saas.parse.time.core;

import kfang.agent.feature.saas.parse.time.entity.TimeParseEntity;

/**
 * 盘客 英文时间解析类
 *
 * @author pengqinglong
 * @since 2022/2/15
 */
public class EnglishTimeParse extends TimeParse {

    @Override
    protected TimeParseEntity initParseUnit() {
        TimeParseEntity entity = new TimeParseEntity();
        entity.setYearUnit("y");
        entity.setMonthUnit("M");
        entity.setDayUnit("d");
        entity.setHourUnit("h");
        entity.setMinuteUnit("m");
        entity.setSecondUnit("s");
        return entity;
    }

}