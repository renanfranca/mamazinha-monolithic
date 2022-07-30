package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.BabyProfile;
import com.mamazinha.baby.domain.BreastFeed;
import com.mamazinha.baby.service.dto.BabyProfileDTO;
import com.mamazinha.baby.service.dto.BreastFeedDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BreastFeed} and its DTO {@link BreastFeedDTO}.
 */
@Mapper(componentModel = "spring")
public interface BreastFeedMapper extends EntityMapper<BreastFeedDTO, BreastFeed> {
    @Mapping(target = "babyProfile", source = "babyProfile", qualifiedByName = "babyProfileName")
    BreastFeedDTO toDto(BreastFeed s);

    @Named("babyProfileName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    BabyProfileDTO toDtoBabyProfileName(BabyProfile babyProfile);
}
