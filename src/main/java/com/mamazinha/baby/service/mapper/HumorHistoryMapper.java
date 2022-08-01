package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.*;
import com.mamazinha.baby.service.dto.HumorHistoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link HumorHistory} and its DTO {@link HumorHistoryDTO}.
 */
@Mapper(componentModel = "spring", uses = { BabyProfileMapper.class, HumorMapper.class })
public interface HumorHistoryMapper extends EntityMapper<HumorHistoryDTO, HumorHistory> {
    @Mapping(target = "babyProfile", source = "babyProfile", qualifiedByName = "name")
    @Mapping(target = "humor", source = "humor", qualifiedByName = "description")
    HumorHistoryDTO toDto(HumorHistory s);
}
