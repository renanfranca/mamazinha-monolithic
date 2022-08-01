package com.mamazinha.baby.domain;

import com.mamazinha.baby.domain.enumeration.Place;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Nap.
 */
@Entity
@Table(name = "nap")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Nap implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "start")
    private ZonedDateTime start;

    @Column(name = "jhi_end")
    private ZonedDateTime end;

    @Enumerated(EnumType.STRING)
    @Column(name = "place")
    private Place place;

    @ManyToOne
    private BabyProfile babyProfile;

    @ManyToOne
    private Humor humor;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Nap id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getStart() {
        return this.start;
    }

    public Nap start(ZonedDateTime start) {
        this.setStart(start);
        return this;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return this.end;
    }

    public Nap end(ZonedDateTime end) {
        this.setEnd(end);
        return this;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public Place getPlace() {
        return this.place;
    }

    public Nap place(Place place) {
        this.setPlace(place);
        return this;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public BabyProfile getBabyProfile() {
        return this.babyProfile;
    }

    public void setBabyProfile(BabyProfile babyProfile) {
        this.babyProfile = babyProfile;
    }

    public Nap babyProfile(BabyProfile babyProfile) {
        this.setBabyProfile(babyProfile);
        return this;
    }

    public Humor getHumor() {
        return this.humor;
    }

    public void setHumor(Humor humor) {
        this.humor = humor;
    }

    public Nap humor(Humor humor) {
        this.setHumor(humor);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Nap)) {
            return false;
        }
        return id != null && id.equals(((Nap) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Nap{" +
            "id=" + getId() +
            ", start='" + getStart() + "'" +
            ", end='" + getEnd() + "'" +
            ", place='" + getPlace() + "'" +
            "}";
    }
}
