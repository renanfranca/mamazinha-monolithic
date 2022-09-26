package com.mamazinha.baby.service.dto;

import java.io.Serializable;

public class HumorAverageDTO implements Serializable {

    private Integer dayOfWeek;

    private Integer humorAverage;

    public Integer getHumorAverage() {
        return humorAverage;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public HumorAverageDTO dayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public void setHumorAverage(Integer humorAverage) {
        this.humorAverage = humorAverage;
    }

    public HumorAverageDTO humorAverage(Integer humorAverage) {
        this.humorAverage = humorAverage;
        return this;
    }
}
