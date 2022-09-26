package com.mamazinha.baby.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mamazinha.baby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NapDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NapDTO.class);
        NapDTO napDTO1 = new NapDTO();
        napDTO1.setId(1L);
        NapDTO napDTO2 = new NapDTO();
        assertThat(napDTO1).isNotEqualTo(napDTO2);
        napDTO2.setId(napDTO1.getId());
        assertThat(napDTO1).isEqualTo(napDTO2);
        napDTO2.setId(2L);
        assertThat(napDTO1).isNotEqualTo(napDTO2);
        napDTO1.setId(null);
        assertThat(napDTO1).isNotEqualTo(napDTO2);
    }
}
