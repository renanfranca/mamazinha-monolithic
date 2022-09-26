package com.mamazinha.baby.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mamazinha.baby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HeightTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Height.class);
        Height height1 = new Height();
        height1.setId(1L);
        Height height2 = new Height();
        height2.setId(height1.getId());
        assertThat(height1).isEqualTo(height2);
        height2.setId(2L);
        assertThat(height1).isNotEqualTo(height2);
        height1.setId(null);
        assertThat(height1).isNotEqualTo(height2);
    }
}
