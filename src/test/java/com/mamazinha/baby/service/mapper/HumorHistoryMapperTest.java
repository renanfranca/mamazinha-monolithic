package com.mamazinha.baby.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HumorHistoryMapperTest {

    private HumorHistoryMapper humorHistoryMapper;

    @BeforeEach
    public void setUp() {
        humorHistoryMapper = new HumorHistoryMapperImpl();
    }
}
