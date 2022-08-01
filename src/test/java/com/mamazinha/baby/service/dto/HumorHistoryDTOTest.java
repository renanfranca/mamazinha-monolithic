package com.mamazinha.baby.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mamazinha.baby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HumorHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HumorHistoryDTO.class);
        HumorHistoryDTO humorHistoryDTO1 = new HumorHistoryDTO();
        humorHistoryDTO1.setId(1L);
        HumorHistoryDTO humorHistoryDTO2 = new HumorHistoryDTO();
        assertThat(humorHistoryDTO1).isNotEqualTo(humorHistoryDTO2);
        humorHistoryDTO2.setId(humorHistoryDTO1.getId());
        assertThat(humorHistoryDTO1).isEqualTo(humorHistoryDTO2);
        humorHistoryDTO2.setId(2L);
        assertThat(humorHistoryDTO1).isNotEqualTo(humorHistoryDTO2);
        humorHistoryDTO1.setId(null);
        assertThat(humorHistoryDTO1).isNotEqualTo(humorHistoryDTO2);
    }
}
