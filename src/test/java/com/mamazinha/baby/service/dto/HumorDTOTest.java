package com.mamazinha.baby.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mamazinha.baby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HumorDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HumorDTO.class);
        HumorDTO humorDTO1 = new HumorDTO();
        humorDTO1.setId(1L);
        HumorDTO humorDTO2 = new HumorDTO();
        assertThat(humorDTO1).isNotEqualTo(humorDTO2);
        humorDTO2.setId(humorDTO1.getId());
        assertThat(humorDTO1).isEqualTo(humorDTO2);
        humorDTO2.setId(2L);
        assertThat(humorDTO1).isNotEqualTo(humorDTO2);
        humorDTO1.setId(null);
        assertThat(humorDTO1).isNotEqualTo(humorDTO2);
    }
}
