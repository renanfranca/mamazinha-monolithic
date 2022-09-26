package com.mamazinha.baby.service.dto;

import java.io.Serializable;
import java.util.List;

public class BreastFeedLastCurrentWeekDTO implements Serializable {

    private List<BreastFeedWeekDTO> lastWeekBreastFeeds;

    private List<BreastFeedWeekDTO> currentWeekBreastFeeds;

    public List<BreastFeedWeekDTO> getLastWeekBreastFeeds() {
        return lastWeekBreastFeeds;
    }

    public List<BreastFeedWeekDTO> getCurrentWeekBreastFeeds() {
        return currentWeekBreastFeeds;
    }

    public void setLastWeekBreastFeeds(List<BreastFeedWeekDTO> lastWeekBreastFeeds) {
        this.lastWeekBreastFeeds = lastWeekBreastFeeds;
    }

    public void setCurrentWeekBreastFeeds(List<BreastFeedWeekDTO> currentWeekBreastFeeds) {
        this.currentWeekBreastFeeds = currentWeekBreastFeeds;
    }

    public BreastFeedLastCurrentWeekDTO lastWeekBreastFeeds(List<BreastFeedWeekDTO> lastWeekBreastFeeds) {
        this.lastWeekBreastFeeds = lastWeekBreastFeeds;
        return this;
    }

    public BreastFeedLastCurrentWeekDTO currentWeekBreastFeeds(List<BreastFeedWeekDTO> currentWeekBreastFeeds) {
        this.currentWeekBreastFeeds = currentWeekBreastFeeds;
        return this;
    }
}
