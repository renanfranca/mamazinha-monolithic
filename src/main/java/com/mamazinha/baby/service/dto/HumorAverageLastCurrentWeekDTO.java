package com.mamazinha.baby.service.dto;

import java.io.Serializable;
import java.util.List;

public class HumorAverageLastCurrentWeekDTO implements Serializable {

    private List<HumorAverageDTO> lastWeekHumorAverage;

    private List<HumorAverageDTO> currentWeekHumorAverage;

    public List<HumorAverageDTO> getLastWeekHumorAverage() {
        return lastWeekHumorAverage;
    }

    public List<HumorAverageDTO> getCurrentWeekHumorAverage() {
        return currentWeekHumorAverage;
    }

    public void setLastWeekHumorAverage(List<HumorAverageDTO> lastWeekHumorAverage) {
        this.lastWeekHumorAverage = lastWeekHumorAverage;
    }

    public void setCurrentWeekHumorAverage(List<HumorAverageDTO> currentWeekHumorAverage) {
        this.currentWeekHumorAverage = currentWeekHumorAverage;
    }

    public HumorAverageLastCurrentWeekDTO lastWeekHumorAverage(List<HumorAverageDTO> lastWeekHumorAverage) {
        this.lastWeekHumorAverage = lastWeekHumorAverage;
        return this;
    }

    public HumorAverageLastCurrentWeekDTO currentWeekHumorAverage(List<HumorAverageDTO> currentWeekHumorAverage) {
        this.currentWeekHumorAverage = currentWeekHumorAverage;
        return this;
    }
}
