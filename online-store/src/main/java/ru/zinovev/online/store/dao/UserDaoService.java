package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.controller.dto.ChangePasswordDto;
import ru.zinovev.online.store.controller.dto.UserRegistrationDto;
import ru.zinovev.online.store.controller.dto.UserUpdateDto;
import ru.zinovev.online.store.dao.entity.User;
import ru.zinovev.online.store.dao.mapper.UserMapper;
import ru.zinovev.online.store.dao.repository.UserRepository;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.UserDetails;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDaoService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    //  private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDetails createUser(UserRegistrationDto userRegistrationDto) {
        var newUser = User.builder()
                .publicUserId(UUID.randomUUID().toString())
                .birthday(userRegistrationDto.birthday())
                .email(userRegistrationDto.email())
                .name(userRegistrationDto.name())
                .lastname(userRegistrationDto.lastname())
                .passwordHash(userRegistrationDto.password())
                .build();
        return userMapper.toUserDetails(userRepository.save(newUser));
    }

    @Transactional
    public UserDetails updateUser(UserDetails userDetails, UserUpdateDto userUpdateDto) {
        var user = userRepository.findByPublicUserId(userDetails.publicUserId()).get();
        var updatedUser = userMapper.updateUserFromDto(userUpdateDto, user);
        return userMapper.toUserDetails(userRepository.save(updatedUser));
    }

    @Transactional
    public void deleteUser(UserDetails userDetails) {
        userRepository.delete(userRepository.findByPublicUserId(userDetails.publicUserId()).get());
    }

    @Transactional
    public UserDetails changePassword(UserDetails userDetails, ChangePasswordDto changePasswordDto) {
        var user = userRepository.findByPublicUserId(userDetails.publicUserId()).get();
        var updateUser = user.toBuilder()
                .passwordHash(changePasswordDto.newPassword())
                .build();
        return userMapper.toUserDetails(userRepository.save(updateUser));
    }

    public Boolean findByEmailIgnoreCase(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    public Optional<UserDetails> findByPublicId(String publicUserId) {
        var user = userRepository.findByPublicUserId(publicUserId);
        return user.map(userMapper::toUserDetails);
    }

    public Optional<String> findPasswordHashByPublicId(UserDetails userDetails) {
        return userRepository.findPasswordHashByPublicId(userDetails.publicUserId());
    }

    public User getByPublicId(String publicUserId) {
        return userRepository.findByPublicUserId(publicUserId)
                .orElseThrow(() -> new NotFoundException("User with id - " + publicUserId + " not found"));
    }
}
