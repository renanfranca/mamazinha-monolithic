package com.mamazinha.baby.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mamazinha.baby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BabyProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BabyProfile.class);
        BabyProfile babyProfile1 = new BabyProfile();
        babyProfile1.setId(1L);
        BabyProfile babyProfile2 = new BabyProfile();
        babyProfile2.setId(babyProfile1.getId());
        assertThat(babyProfile1).isEqualTo(babyProfile2);
        babyProfile2.setId(2L);
        assertThat(babyProfile1).isNotEqualTo(babyProfile2);
        babyProfile1.setId(null);
        assertThat(babyProfile1).isNotEqualTo(babyProfile2);
    }
}
