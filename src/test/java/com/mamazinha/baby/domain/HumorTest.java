package com.mamazinha.baby.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mamazinha.baby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HumorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Humor.class);
        Humor humor1 = new Humor();
        humor1.setId(1L);
        Humor humor2 = new Humor();
        humor2.setId(humor1.getId());
        assertThat(humor1).isEqualTo(humor2);
        humor2.setId(2L);
        assertThat(humor1).isNotEqualTo(humor2);
        humor1.setId(null);
        assertThat(humor1).isNotEqualTo(humor2);
    }
}
