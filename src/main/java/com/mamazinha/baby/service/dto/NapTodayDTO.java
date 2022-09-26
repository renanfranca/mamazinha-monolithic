package com.mamazinha.baby.service.dto;

import java.io.Serializable;

public class NapTodayDTO implements Serializable {

    private Double sleepHours;

    private Integer sleepHoursGoal;

    public Double getSleepHours() {
        return sleepHours;
    }

    public Integer getSleepHoursGoal() {
        return sleepHoursGoal;
    }

    public void setSleepHoursGoal(Integer sleepHoursGoal) {
        this.sleepHoursGoal = sleepHoursGoal;
    }

    public void setSleepHours(Double sleepHours) {
        this.sleepHours = sleepHours;
    }

    public NapTodayDTO sleepHoursGoal(Integer sleepHoursGoal) {
        this.sleepHoursGoal = sleepHoursGoal;
        return this;
    }

    public NapTodayDTO sleepHours(Double sleepHours) {
        this.sleepHours = sleepHours;
        return this;
    }
}
