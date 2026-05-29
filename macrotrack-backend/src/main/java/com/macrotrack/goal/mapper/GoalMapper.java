package com.macrotrack.goal.mapper;

import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.goal.model.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import com.macrotrack.goal.dto.GoalRequest;

@Mapper(componentModel = "spring")
public interface GoalMapper {
    GoalResponse toResponse(Goal goal);

    Goal toEntity(GoalRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(GoalRequest dto, @MappingTarget Goal entity);
}
