package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.BabyProfile;
import com.mamazinha.baby.domain.Humor;
import com.mamazinha.baby.domain.Nap;
import com.mamazinha.baby.service.dto.BabyProfileDTO;
import com.mamazinha.baby.service.dto.HumorDTO;
import com.mamazinha.baby.service.dto.NapDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Nap} and its DTO {@link NapDTO}.
 */
@Mapper(componentModel = "spring")
public interface NapMapper extends EntityMapper<NapDTO, Nap> {
    @Mapping(target = "babyProfile", source = "babyProfile", qualifiedByName = "babyProfileName")
    @Mapping(target = "humor", source = "humor", qualifiedByName = "humorDescription")
    NapDTO toDto(Nap s);

    @Named("babyProfileName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "main", source = "main")
    BabyProfileDTO toDtoBabyProfileName(BabyProfile babyProfile);

    @Named("humorDescription")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    HumorDTO toDtoHumorDescription(Humor humor);
}
