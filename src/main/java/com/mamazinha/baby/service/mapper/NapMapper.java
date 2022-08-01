package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.*;
import com.mamazinha.baby.service.dto.NapDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Nap} and its DTO {@link NapDTO}.
 */
@Mapper(componentModel = "spring", uses = { BabyProfileMapper.class, HumorMapper.class })
public interface NapMapper extends EntityMapper<NapDTO, Nap> {
    @Mapping(target = "babyProfile", source = "babyProfile", qualifiedByName = "name")
    @Mapping(target = "humor", source = "humor", qualifiedByName = "description")
    NapDTO toDto(Nap s);
}
