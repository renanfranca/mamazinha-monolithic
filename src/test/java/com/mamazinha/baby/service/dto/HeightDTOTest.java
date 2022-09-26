package com.mamazinha.baby.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mamazinha.baby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HeightDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HeightDTO.class);
        HeightDTO heightDTO1 = new HeightDTO();
        heightDTO1.setId(1L);
        HeightDTO heightDTO2 = new HeightDTO();
        assertThat(heightDTO1).isNotEqualTo(heightDTO2);
        heightDTO2.setId(heightDTO1.getId());
        assertThat(heightDTO1).isEqualTo(heightDTO2);
        heightDTO2.setId(2L);
        assertThat(heightDTO1).isNotEqualTo(heightDTO2);
        heightDTO1.setId(null);
        assertThat(heightDTO1).isNotEqualTo(heightDTO2);
    }
}
