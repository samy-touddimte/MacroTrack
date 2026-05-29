package com.macrotrack.user.mapper;

import com.macrotrack.shared.mapper.DateMapper;

import com.macrotrack.user.dto.UserResponse;
import com.macrotrack.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import com.macrotrack.user.dto.UpdateUserRequest;

@Mapper(componentModel = "spring", uses = {DateMapper.class})
public interface UserMapper {
    UserResponse toResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateUserRequest dto, @MappingTarget User entity);
}
