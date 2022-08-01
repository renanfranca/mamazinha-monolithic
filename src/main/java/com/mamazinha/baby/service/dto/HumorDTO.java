package com.mamazinha.baby.service.dto;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mamazinha.baby.domain.Humor} entity.
 */
public class HumorDTO implements Serializable {

    private Long id;

    /**
     * 1 to 5  ANGRY, SAD, CALM, HAPPY, EXCITED
     */
    @NotNull
    @ApiModelProperty(value = "1 to 5  ANGRY, SAD, CALM, HAPPY, EXCITED", required = true)
    private Integer value;

    @NotNull
    private String description;

    @Lob
    private byte[] emotico;

    private String emoticoContentType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getEmotico() {
        return emotico;
    }

    public void setEmotico(byte[] emotico) {
        this.emotico = emotico;
    }

    public String getEmoticoContentType() {
        return emoticoContentType;
    }

    public void setEmoticoContentType(String emoticoContentType) {
        this.emoticoContentType = emoticoContentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HumorDTO)) {
            return false;
        }

        HumorDTO humorDTO = (HumorDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, humorDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HumorDTO{" +
            "id=" + getId() +
            ", value=" + getValue() +
            ", description='" + getDescription() + "'" +
            ", emotico='" + getEmotico() + "'" +
            "}";
    }
}
