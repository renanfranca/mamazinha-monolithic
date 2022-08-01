package com.mamazinha.baby.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.CollectionUtils;

public class NapLastCurrentWeekDTO implements Serializable {

    private List<NapWeekDTO> lastWeekNaps;

    private List<NapWeekDTO> currentWeekNaps;

    private Integer sleepHoursGoal;

    public List<NapWeekDTO> getLastWeekNaps() {
        return lastWeekNaps;
    }

    public List<NapWeekDTO> getCurrentWeekNaps() {
        return currentWeekNaps;
    }

    public Integer getSleepHoursGoal() {
        return sleepHoursGoal;
    }

    public void setSleepHoursGoal(Integer sleepHoursGoal) {
        this.sleepHoursGoal = sleepHoursGoal;
    }

    public void setCurrentWeekNaps(List<NapWeekDTO> currentWeek) {
        this.currentWeekNaps = currentWeek;
    }

    public void setLastWeekNaps(List<NapWeekDTO> lastWeek) {
        this.lastWeekNaps = lastWeek;
    }

    public NapLastCurrentWeekDTO sleepHoursGoal(Integer sleepHoursGoal) {
        this.sleepHoursGoal = sleepHoursGoal;
        return this;
    }

    public NapLastCurrentWeekDTO currentWeekNaps(List<NapWeekDTO> currentWeekNaps) {
        this.currentWeekNaps = currentWeekNaps;
        return this;
    }

    public NapLastCurrentWeekDTO lastWeekNaps(List<NapWeekDTO> lastWeekNaps) {
        this.lastWeekNaps = lastWeekNaps;
        return this;
    }

    public NapLastCurrentWeekDTO currentWeekNap(NapWeekDTO currentWeekNap) {
        if (CollectionUtils.isEmpty(this.currentWeekNaps)) {
            this.currentWeekNaps = new ArrayList<>();
        }
        this.currentWeekNaps.add(currentWeekNap);
        return this;
    }

    public NapLastCurrentWeekDTO lastWeekNap(NapWeekDTO lastWeekNap) {
        if (CollectionUtils.isEmpty(this.lastWeekNaps)) {
            this.lastWeekNaps = new ArrayList<>();
        }
        this.lastWeekNaps.add(lastWeekNap);
        return this;
    }
}
