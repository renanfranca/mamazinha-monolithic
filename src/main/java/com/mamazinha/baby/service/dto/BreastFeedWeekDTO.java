package com.mamazinha.baby.service.dto;

import java.io.Serializable;

public class BreastFeedWeekDTO implements Serializable {

    private Integer dayOfWeek;

    private Double averageFeedHours;

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public Double getAverageFeedHours() {
        return averageFeedHours;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setAverageFeedHours(Double averageFeedHours) {
        this.averageFeedHours = averageFeedHours;
    }

    public BreastFeedWeekDTO dayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public BreastFeedWeekDTO averageFeedHours(Double averageFeedHours) {
        this.averageFeedHours = averageFeedHours;
        return this;
    }
}
