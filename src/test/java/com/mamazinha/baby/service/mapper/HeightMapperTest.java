package com.mamazinha.baby.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HeightMapperTest {

    private HeightMapper heightMapper;

    @BeforeEach
    public void setUp() {
        heightMapper = new HeightMapperImpl();
    }
}
