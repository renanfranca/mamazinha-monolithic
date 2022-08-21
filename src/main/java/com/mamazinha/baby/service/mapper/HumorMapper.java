package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.Humor;
import com.mamazinha.baby.service.dto.HumorDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Humor} and its DTO {@link HumorDTO}.
 */
@Mapper(componentModel = "spring")
public interface HumorMapper extends EntityMapper<HumorDTO, Humor> {}
