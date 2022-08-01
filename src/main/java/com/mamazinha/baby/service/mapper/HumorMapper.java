package com.mamazinha.baby.service.mapper;

import com.mamazinha.baby.domain.Humor;
import com.mamazinha.baby.service.dto.HumorDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Humor} and its DTO {@link HumorDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface HumorMapper extends EntityMapper<HumorDTO, Humor> {
    @Named("description")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "emotico", source = "emotico")
    @Mapping(target = "emoticoContentType", source = "emoticoContentType")
    HumorDTO toDtoDescription(Humor humor);
}
