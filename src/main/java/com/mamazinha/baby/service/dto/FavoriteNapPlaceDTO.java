package com.mamazinha.baby.service.dto;

import com.mamazinha.baby.domain.enumeration.Place;

public class FavoriteNapPlaceDTO {

    private Integer periodInDays;

    private Place favoritePlace;

    private Long amountOfTimes;

    public Integer getPeriodInDays() {
        return periodInDays;
    }

    public void setAmountOfTimes(Long amountOfTimes) {
        this.amountOfTimes = amountOfTimes;
    }

    public Place getFavoritePlace() {
        return favoritePlace;
    }

    public void setPeriodInDays(Integer periodInDays) {
        this.periodInDays = periodInDays;
    }

    public void setFavoritePlace(Place favoritePlace) {
        this.favoritePlace = favoritePlace;
    }

    public Long getAmountOfTimes() {
        return amountOfTimes;
    }

    public FavoriteNapPlaceDTO periodInDays(Integer periodInDays) {
        this.periodInDays = periodInDays;
        return this;
    }

    public FavoriteNapPlaceDTO favoritePlace(Place favoritePlace) {
        this.favoritePlace = favoritePlace;
        return this;
    }

    public FavoriteNapPlaceDTO amountOfTimes(Long amountOfTimes) {
        this.amountOfTimes = amountOfTimes;
        return this;
    }
}
