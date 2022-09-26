package com.mamazinha.baby.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Humor.
 */
@Entity
@Table(name = "humor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Humor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 1 to 5  ANGRY, SAD, CALM, HAPPY, EXCITED
     */
    @NotNull
    @Column(name = "value", nullable = false)
    private Integer value;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @Lob
    @Column(name = "emotico")
    private byte[] emotico;

    @Column(name = "emotico_content_type")
    private String emoticoContentType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Humor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getValue() {
        return this.value;
    }

    public Humor value(Integer value) {
        this.setValue(value);
        return this;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getDescription() {
        return this.description;
    }

    public Humor description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getEmotico() {
        return this.emotico;
    }

    public Humor emotico(byte[] emotico) {
        this.setEmotico(emotico);
        return this;
    }

    public void setEmotico(byte[] emotico) {
        this.emotico = emotico;
    }

    public String getEmoticoContentType() {
        return this.emoticoContentType;
    }

    public Humor emoticoContentType(String emoticoContentType) {
        this.emoticoContentType = emoticoContentType;
        return this;
    }

    public void setEmoticoContentType(String emoticoContentType) {
        this.emoticoContentType = emoticoContentType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Humor)) {
            return false;
        }
        return id != null && id.equals(((Humor) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Humor{" +
            "id=" + getId() +
            ", value=" + getValue() +
            ", description='" + getDescription() + "'" +
            ", emotico='" + getEmotico() + "'" +
            ", emoticoContentType='" + getEmoticoContentType() + "'" +
            "}";
    }
}
