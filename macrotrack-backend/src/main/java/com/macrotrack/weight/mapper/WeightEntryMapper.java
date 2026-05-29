package com.macrotrack.weight.mapper;

import com.macrotrack.shared.mapper.DateMapper;

import com.macrotrack.weight.dto.WeightEntryResponse;
import com.macrotrack.weight.model.WeightEntry;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import com.macrotrack.weight.dto.WeightEntryRequest;

@Mapper(componentModel = "spring", uses = {DateMapper.class})
public interface WeightEntryMapper {
    WeightEntryResponse toResponse(WeightEntry weightEntry);

    WeightEntry toEntity(WeightEntryRequest dto);

    void updateEntityFromDto(WeightEntryRequest dto, @MappingTarget WeightEntry entity);
}
