package com.mamazinha.baby.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HumorMapperTest {

    private HumorMapper humorMapper;

    @BeforeEach
    public void setUp() {
        humorMapper = new HumorMapperImpl();
    }
}
