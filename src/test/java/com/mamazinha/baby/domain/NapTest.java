package com.mamazinha.baby.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mamazinha.baby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NapTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Nap.class);
        Nap nap1 = new Nap();
        nap1.setId(1L);
        Nap nap2 = new Nap();
        nap2.setId(nap1.getId());
        assertThat(nap1).isEqualTo(nap2);
        nap2.setId(2L);
        assertThat(nap1).isNotEqualTo(nap2);
        nap1.setId(null);
        assertThat(nap1).isNotEqualTo(nap2);
    }
}
