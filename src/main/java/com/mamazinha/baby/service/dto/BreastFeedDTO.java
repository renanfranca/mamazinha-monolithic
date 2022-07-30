package com.mamazinha.baby.service.dto;

import com.mamazinha.baby.domain.enumeration.Pain;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.mamazinha.baby.domain.BreastFeed} entity.
 */
public class BreastFeedDTO implements Serializable {

    private Long id;

    private ZonedDateTime start;

    private ZonedDateTime end;

    private Pain pain;

    private BabyProfileDTO babyProfile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public Pain getPain() {
        return pain;
    }

    public void setPain(Pain pain) {
        this.pain = pain;
    }

    public BabyProfileDTO getBabyProfile() {
        return babyProfile;
    }

    public void setBabyProfile(BabyProfileDTO babyProfile) {
        this.babyProfile = babyProfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BreastFeedDTO)) {
            return false;
        }

        BreastFeedDTO breastFeedDTO = (BreastFeedDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, breastFeedDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BreastFeedDTO{" +
            "id=" + getId() +
            ", start='" + getStart() + "'" +
            ", end='" + getEnd() + "'" +
            ", pain='" + getPain() + "'" +
            ", babyProfile=" + getBabyProfile() +
            "}";
    }
}
