package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.BabyProfile;
import com.mamazinha.baby.domain.Weight;
import com.mamazinha.baby.service.dto.BabyProfileDTO;
import com.mamazinha.baby.service.dto.WeightDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Weight} and its DTO {@link WeightDTO}.
 */
@Mapper(componentModel = "spring")
public interface WeightMapper extends EntityMapper<WeightDTO, Weight> {
    @Mapping(target = "babyProfile", source = "babyProfile", qualifiedByName = "babyProfileName")
    WeightDTO toDto(Weight s);

    @Named("babyProfileName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    BabyProfileDTO toDtoBabyProfileName(BabyProfile babyProfile);
}
