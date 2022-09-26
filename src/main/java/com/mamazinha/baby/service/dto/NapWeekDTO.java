package com.mamazinha.baby.service.dto;

import java.io.Serializable;

public class NapWeekDTO implements Serializable {

    private Integer dayOfWeek;

    private Double sleepHours;

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public Double getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(Double sleepHours) {
        this.sleepHours = sleepHours;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public NapWeekDTO sleepHours(Double sleepHours) {
        this.sleepHours = sleepHours;
        return this;
    }

    public NapWeekDTO dayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }
}
