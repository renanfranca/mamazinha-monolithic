package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.BabyProfile;
import com.mamazinha.baby.domain.Humor;
import com.mamazinha.baby.domain.HumorHistory;
import com.mamazinha.baby.service.dto.BabyProfileDTO;
import com.mamazinha.baby.service.dto.HumorDTO;
import com.mamazinha.baby.service.dto.HumorHistoryDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link HumorHistory} and its DTO {@link HumorHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface HumorHistoryMapper extends EntityMapper<HumorHistoryDTO, HumorHistory> {
    @Mapping(target = "babyProfile", source = "babyProfile", qualifiedByName = "babyProfileName")
    @Mapping(target = "humor", source = "humor", qualifiedByName = "humorDescription")
    HumorHistoryDTO toDto(HumorHistory s);

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
