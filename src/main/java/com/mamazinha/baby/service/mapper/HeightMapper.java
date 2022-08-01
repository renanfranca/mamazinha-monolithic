package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.*;
import com.mamazinha.baby.service.dto.HeightDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Height} and its DTO {@link HeightDTO}.
 */
@Mapper(componentModel = "spring", uses = { BabyProfileMapper.class })
public interface HeightMapper extends EntityMapper<HeightDTO, Height> {
    @Mapping(target = "babyProfile", source = "babyProfile", qualifiedByName = "name")
    HeightDTO toDto(Height s);
}
