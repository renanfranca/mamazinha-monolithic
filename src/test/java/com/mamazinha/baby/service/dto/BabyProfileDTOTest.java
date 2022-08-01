package com.mamazinha.baby.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mamazinha.baby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BabyProfileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BabyProfileDTO.class);
        BabyProfileDTO babyProfileDTO1 = new BabyProfileDTO();
        babyProfileDTO1.setId(1L);
        BabyProfileDTO babyProfileDTO2 = new BabyProfileDTO();
        assertThat(babyProfileDTO1).isNotEqualTo(babyProfileDTO2);
        babyProfileDTO2.setId(babyProfileDTO1.getId());
        assertThat(babyProfileDTO1).isEqualTo(babyProfileDTO2);
        babyProfileDTO2.setId(2L);
        assertThat(babyProfileDTO1).isNotEqualTo(babyProfileDTO2);
        babyProfileDTO1.setId(null);
        assertThat(babyProfileDTO1).isNotEqualTo(babyProfileDTO2);
    }
}
