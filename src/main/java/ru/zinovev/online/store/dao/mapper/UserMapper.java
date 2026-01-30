package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.zinovev.online.store.controller.dto.UserRegistrationDto;
import ru.zinovev.online.store.controller.dto.UserUpdateDto;
import ru.zinovev.online.store.dao.entity.CustomerView;
import ru.zinovev.online.store.dao.entity.User;
import ru.zinovev.online.store.model.TopCustomerDetails;
import ru.zinovev.online.store.model.UserDetails;
import ru.zinovev.online.store.model.UserRegistrationDetails;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDetails toUserDetails(User user);

    UserRegistrationDetails toUserRegistrationDetails(UserRegistrationDto userRegistrationDto);

    @Mapping(target = "publicUserId", ignore = true)
    UserDetails toUserDetails(UserUpdateDto userUpdateDto);

    TopCustomerDetails toTopCustomerDetails(CustomerView customerView);

    default User updateUserFromDto(UserUpdateDto dto, User user) {
        return user.toBuilder()
                .name(dto.name() != null && !dto.name().isEmpty() ? dto.name() : user.getName())
                .lastname(dto.lastname() != null && !dto.lastname().isEmpty() ? dto.lastname() : user.getLastname())
                .email(dto.email() != null && !dto.email().isEmpty() ? dto.email() : user.getEmail())
                .build();
    }
}
