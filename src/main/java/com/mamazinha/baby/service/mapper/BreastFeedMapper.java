package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.*;
import com.mamazinha.baby.service.dto.BreastFeedDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BreastFeed} and its DTO {@link BreastFeedDTO}.
 */
@Mapper(componentModel = "spring", uses = { BabyProfileMapper.class })
public interface BreastFeedMapper extends EntityMapper<BreastFeedDTO, BreastFeed> {
    @Mapping(target = "babyProfile", source = "babyProfile", qualifiedByName = "name")
    BreastFeedDTO toDto(BreastFeed s);
}
