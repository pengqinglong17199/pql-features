package kfang.agent.feature.saas.parse.time.core;

import cn.hyugatool.core.date.DateUtil;
import kfang.agent.feature.saas.parse.AgentParse;
import kfang.agent.feature.saas.parse.time.constant.LimitTime;
import kfang.agent.feature.saas.parse.time.entity.TimeParseEntity;
import kfang.infra.common.util.CommonDateUtil;

import java.util.Date;
import java.util.Objects;

/**
 * 盘客时间格式解析器
 *
 * @author pengqinglong
 * @since 2022/2/15
 */
public abstract class TimeParse implements AgentParse<String, Long> {

    /**
     * 初始化parse的规则
     *
     * @return 时间解析实体
     */
    protected abstract TimeParseEntity initParseUnit();

    @Override
    public Long parse(String time) {

        TimeParseEntity entity = this.initParseUnit();

        // 处理数据
        this.handle(time, entity);

        // 解析数据
        return this.calculate(entity);
    }

    /**
     * 格式是否符合要求
     */
    protected void handle(String time, TimeParseEntity entity) {
        if (Objects.equals(LimitTime.TO_DAY, time)) {
            entity.setToDay(true);
            return;
        }

        if (Objects.equals(LimitTime.TO_DAY_NOW, time)) {
            entity.setDay(1);
            return;
        }
        // 快照增加一个占位符 防止越界
        String snapShoot = time + "#";

        snapShoot = this.formatYear(snapShoot, entity);

        snapShoot = this.formatMonth(snapShoot, entity);

        snapShoot = this.formatDay(snapShoot, entity);

        snapShoot = this.formatHour(snapShoot, entity);

        snapShoot = this.formatMinute(snapShoot, entity);

        this.formatSecond(snapShoot, entity);
    }

    /**
     * 计算结果
     */
    protected Long calculate(TimeParseEntity entity) {
        if (entity.isToDay()) {
            return (long) CommonDateUtil.getTodayLeftSeconds();
        }
        Date now = new Date();
        Date date = new Date();
        date = DateUtil.moreOrLessYears(date, entity.getYear());
        date = DateUtil.moreOrLessMonths(date, entity.getMonth());
        date = DateUtil.moreOrLessDays(date, entity.getDay());
        date = DateUtil.moreOrLessHours(date, entity.getHour());
        date = DateUtil.moreOrLessMinutes(date, entity.getMinute());
        date = DateUtil.moreOrLessSeconds(date, entity.getSecond());
        return DateUtil.secondDifference(now, date);
    }

    private void formatSecond(String time, TimeParseEntity entity) {
        int i = time.indexOf(entity.getSecondUnit());
        if (i == -1) {
            entity.setSecond(0);
            return;
        }
        String[] split = time.split(entity.getSecondUnit());
        entity.setSecond(Integer.parseInt(split[0]));
    }

    private String formatMinute(String time, TimeParseEntity entity) {
        int i = time.indexOf(entity.getMinuteUnit());
        if (i == -1) {
            entity.setMinute(0);
            return time;
        }
        String[] split = time.split(entity.getMinuteUnit());
        entity.setMinute(Integer.parseInt(split[0]));
        return split[1];
    }

    private String formatHour(String time, TimeParseEntity entity) {
        int i = time.indexOf(entity.getHourUnit());
        if (i == -1) {
            entity.setHour(0);
            return time;
        }
        String[] split = time.split(entity.getHourUnit());
        entity.setHour(Integer.parseInt(split[0]));
        return split[1];
    }

    private String formatDay(String time, TimeParseEntity entity) {
        int i = time.indexOf(entity.getDayUnit());
        if (i == -1) {
            entity.setDay(0);
            return time;
        }
        String[] split = time.split(entity.getDayUnit());
        entity.setDay(Integer.parseInt(split[0]));
        return split[1];
    }

    private String formatMonth(String time, TimeParseEntity entity) {
        int i = time.indexOf(entity.getMonthUnit());
        if (i == -1) {
            entity.setMonth(0);
            return time;
        }
        String[] split = time.split(entity.getMonthUnit());
        entity.setMonth(Integer.parseInt(split[0]));
        return split[1];
    }

    private String formatYear(String time, TimeParseEntity entity) {
        int i = time.indexOf(entity.getYearUnit());
        if (i == -1) {
            entity.setYear(0);
            return time;
        }
        String[] split = time.split(entity.getYearUnit());
        entity.setYear(Integer.parseInt(split[0]));
        return split[1];
    }
}