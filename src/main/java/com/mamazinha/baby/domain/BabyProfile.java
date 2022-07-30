package com.mamazinha.baby.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BabyProfile.
 */
@Entity
@Table(name = "baby_profile")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BabyProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "picture")
    private byte[] picture;

    @Column(name = "picture_content_type")
    private String pictureContentType;

    @NotNull
    @Column(name = "birthday", nullable = false)
    private ZonedDateTime birthday;

    @Column(name = "sign")
    private String sign;

    @Column(name = "main")
    private Boolean main;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private String userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BabyProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public BabyProfile name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getPicture() {
        return this.picture;
    }

    public BabyProfile picture(byte[] picture) {
        this.setPicture(picture);
        return this;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getPictureContentType() {
        return this.pictureContentType;
    }

    public BabyProfile pictureContentType(String pictureContentType) {
        this.pictureContentType = pictureContentType;
        return this;
    }

    public void setPictureContentType(String pictureContentType) {
        this.pictureContentType = pictureContentType;
    }

    public ZonedDateTime getBirthday() {
        return this.birthday;
    }

    public BabyProfile birthday(ZonedDateTime birthday) {
        this.setBirthday(birthday);
        return this;
    }

    public void setBirthday(ZonedDateTime birthday) {
        this.birthday = birthday;
    }

    public String getSign() {
        return this.sign;
    }

    public BabyProfile sign(String sign) {
        this.setSign(sign);
        return this;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Boolean getMain() {
        return this.main;
    }

    public BabyProfile main(Boolean main) {
        this.setMain(main);
        return this;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }

    public String getUserId() {
        return this.userId;
    }

    public BabyProfile userId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BabyProfile)) {
            return false;
        }
        return id != null && id.equals(((BabyProfile) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BabyProfile{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", picture='" + getPicture() + "'" +
            ", pictureContentType='" + getPictureContentType() + "'" +
            ", birthday='" + getBirthday() + "'" +
            ", sign='" + getSign() + "'" +
            ", main='" + getMain() + "'" +
            ", userId='" + getUserId() + "'" +
            "}";
    }
}
