package com.mamazinha.baby.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mamazinha.baby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BreastFeedDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BreastFeedDTO.class);
        BreastFeedDTO breastFeedDTO1 = new BreastFeedDTO();
        breastFeedDTO1.setId(1L);
        BreastFeedDTO breastFeedDTO2 = new BreastFeedDTO();
        assertThat(breastFeedDTO1).isNotEqualTo(breastFeedDTO2);
        breastFeedDTO2.setId(breastFeedDTO1.getId());
        assertThat(breastFeedDTO1).isEqualTo(breastFeedDTO2);
        breastFeedDTO2.setId(2L);
        assertThat(breastFeedDTO1).isNotEqualTo(breastFeedDTO2);
        breastFeedDTO1.setId(null);
        assertThat(breastFeedDTO1).isNotEqualTo(breastFeedDTO2);
    }
}
