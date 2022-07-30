package com.mamazinha.baby.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NapMapperTest {

    private NapMapper napMapper;

    @BeforeEach
    public void setUp() {
        napMapper = new NapMapperImpl();
    }
}
