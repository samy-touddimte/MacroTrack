package com.macrotrack.nutrition.mapper;

import com.macrotrack.nutrition.dto.FoodLogResponse;
import com.macrotrack.nutrition.model.FoodLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import com.macrotrack.nutrition.dto.FoodLogRequest;

@Mapper(componentModel = "spring")
public interface FoodLogMapper {
    FoodLogResponse toResponse(FoodLog foodLog);

    @Mapping(source = "caloriesKcal", target = "caloriesKcal")
    FoodLog toEntity(FoodLogRequest dto);
}
