package com.mamazinha.baby.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BabyProfileMapperTest {

    private BabyProfileMapper babyProfileMapper;

    @BeforeEach
    public void setUp() {
        babyProfileMapper = new BabyProfileMapperImpl();
    }
}
