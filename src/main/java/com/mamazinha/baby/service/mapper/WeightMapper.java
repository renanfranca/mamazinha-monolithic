package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.*;
import com.mamazinha.baby.service.dto.WeightDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Weight} and its DTO {@link WeightDTO}.
 */
@Mapper(componentModel = "spring", uses = { BabyProfileMapper.class })
public interface WeightMapper extends EntityMapper<WeightDTO, Weight> {
    @Mapping(target = "babyProfile", source = "babyProfile", qualifiedByName = "name")
    WeightDTO toDto(Weight s);
}
