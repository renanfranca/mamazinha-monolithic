package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.BabyProfile;
import com.mamazinha.baby.service.dto.BabyProfileDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link BabyProfile} and its DTO {@link BabyProfileDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface BabyProfileMapper extends EntityMapper<BabyProfileDTO, BabyProfile> {
    @Named("name")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "main", source = "main")
    BabyProfileDTO toDtoName(BabyProfile babyProfile);
}
