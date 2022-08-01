package com.mamazinha.baby.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mamazinha.baby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WeightDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WeightDTO.class);
        WeightDTO weightDTO1 = new WeightDTO();
        weightDTO1.setId(1L);
        WeightDTO weightDTO2 = new WeightDTO();
        assertThat(weightDTO1).isNotEqualTo(weightDTO2);
        weightDTO2.setId(weightDTO1.getId());
        assertThat(weightDTO1).isEqualTo(weightDTO2);
        weightDTO2.setId(2L);
        assertThat(weightDTO1).isNotEqualTo(weightDTO2);
        weightDTO1.setId(null);
        assertThat(weightDTO1).isNotEqualTo(weightDTO2);
    }
}
