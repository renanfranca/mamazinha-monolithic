package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.BabyProfile;
import com.mamazinha.baby.domain.Weight;
import com.mamazinha.baby.service.dto.BabyProfileDTO;
import com.mamazinha.baby.service.dto.WeightDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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
    @Mapping(target = "main", source = "main")
    BabyProfileDTO toDtoBabyProfileName(BabyProfile babyProfile);
}
