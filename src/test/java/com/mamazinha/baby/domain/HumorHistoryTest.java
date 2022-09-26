package com.mamazinha.baby.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mamazinha.baby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HumorHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HumorHistory.class);
        HumorHistory humorHistory1 = new HumorHistory();
        humorHistory1.setId(1L);
        HumorHistory humorHistory2 = new HumorHistory();
        humorHistory2.setId(humorHistory1.getId());
        assertThat(humorHistory1).isEqualTo(humorHistory2);
        humorHistory2.setId(2L);
        assertThat(humorHistory1).isNotEqualTo(humorHistory2);
        humorHistory1.setId(null);
        assertThat(humorHistory1).isNotEqualTo(humorHistory2);
    }
}
