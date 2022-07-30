package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.BabyProfile;
import com.mamazinha.baby.domain.Height;
import com.mamazinha.baby.service.dto.BabyProfileDTO;
import com.mamazinha.baby.service.dto.HeightDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Height} and its DTO {@link HeightDTO}.
 */
@Mapper(componentModel = "spring")
public interface HeightMapper extends EntityMapper<HeightDTO, Height> {
    @Mapping(target = "babyProfile", source = "babyProfile", qualifiedByName = "babyProfileName")
    HeightDTO toDto(Height s);

    @Named("babyProfileName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    BabyProfileDTO toDtoBabyProfileName(BabyProfile babyProfile);
}
