package com.mamazinha.baby.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WeightMapperTest {

    private WeightMapper weightMapper;

    @BeforeEach
    public void setUp() {
        weightMapper = new WeightMapperImpl();
    }
}
