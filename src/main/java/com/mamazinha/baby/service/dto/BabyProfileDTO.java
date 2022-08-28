package com.mamazinha.baby.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

/**
 * A DTO for the {@link com.mamazinha.baby.domain.BabyProfile} entity.
 */
public class BabyProfileDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @Lob
    private byte[] picture;

    private String pictureContentType;

    @NotNull
    private ZonedDateTime birthday;

    private String sign;

    private Boolean main;

    private String userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getPictureContentType() {
        return pictureContentType;
    }

    public void setPictureContentType(String pictureContentType) {
        this.pictureContentType = pictureContentType;
    }

    public ZonedDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(ZonedDateTime birthday) {
        this.birthday = birthday;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Boolean getMain() {
        return main;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BabyProfileDTO)) {
            return false;
        }

        BabyProfileDTO babyProfileDTO = (BabyProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, babyProfileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BabyProfileDTO{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                ", picture='" + getPicture() + "'" +
                ", birthday='" + getBirthday() + "'" +
                ", sign='" + getSign() + "'" +
                ", main='" + getMain() + "'" +
                ", userId='" + getUserId() + "'" +
                "}";
    }
}
