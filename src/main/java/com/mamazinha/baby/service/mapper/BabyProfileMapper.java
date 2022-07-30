package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.BabyProfile;
import com.mamazinha.baby.service.dto.BabyProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BabyProfile} and its DTO {@link BabyProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface BabyProfileMapper extends EntityMapper<BabyProfileDTO, BabyProfile> {}
