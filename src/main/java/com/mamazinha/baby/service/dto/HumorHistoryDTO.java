package com.mamazinha.baby.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.mamazinha.baby.domain.HumorHistory} entity.
 */
public class HumorHistoryDTO implements Serializable {

    private Long id;

    private ZonedDateTime date;

    private BabyProfileDTO babyProfile;

    private HumorDTO humor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public BabyProfileDTO getBabyProfile() {
        return babyProfile;
    }

    public void setBabyProfile(BabyProfileDTO babyProfile) {
        this.babyProfile = babyProfile;
    }

    public HumorDTO getHumor() {
        return humor;
    }

    public void setHumor(HumorDTO humor) {
        this.humor = humor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HumorHistoryDTO)) {
            return false;
        }

        HumorHistoryDTO humorHistoryDTO = (HumorHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, humorHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HumorHistoryDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", babyProfile=" + getBabyProfile() +
            ", humor=" + getHumor() +
            "}";
    }
}
