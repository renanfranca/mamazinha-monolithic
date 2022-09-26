package com.mamazinha.baby.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mamazinha.baby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BreastFeedTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BreastFeed.class);
        BreastFeed breastFeed1 = new BreastFeed();
        breastFeed1.setId(1L);
        BreastFeed breastFeed2 = new BreastFeed();
        breastFeed2.setId(breastFeed1.getId());
        assertThat(breastFeed1).isEqualTo(breastFeed2);
        breastFeed2.setId(2L);
        assertThat(breastFeed1).isNotEqualTo(breastFeed2);
        breastFeed1.setId(null);
        assertThat(breastFeed1).isNotEqualTo(breastFeed2);
    }
}
