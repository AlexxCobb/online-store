package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.zinovev.online.store.controller.dto.UserUpdateDto;
import ru.zinovev.online.store.dao.entity.User;
import ru.zinovev.online.store.model.UserDetails;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserDetails userDetails);

    UserDetails toUserDetails(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateUserFromUserUpdateDto(UserUpdateDto userUpdateDto, @MappingTarget User user);
}
