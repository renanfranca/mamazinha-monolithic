package com.mamazinha.baby.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BreastFeedMapperTest {

    private BreastFeedMapper breastFeedMapper;

    @BeforeEach
    public void setUp() {
        breastFeedMapper = new BreastFeedMapperImpl();
    }
}
