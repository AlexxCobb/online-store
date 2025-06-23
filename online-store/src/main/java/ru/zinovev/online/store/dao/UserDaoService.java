package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.controller.dto.ChangePasswordDto;
import ru.zinovev.online.store.controller.dto.UserRegistrationDto;
import ru.zinovev.online.store.controller.dto.UserUpdateDto;
import ru.zinovev.online.store.dao.entity.User;
import ru.zinovev.online.store.dao.mapper.UserMapper;
import ru.zinovev.online.store.dao.repository.UserRepository;
import ru.zinovev.online.store.model.UserDetails;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDaoService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDetails createUser(UserRegistrationDto userRegistrationDto) {
        var newUser = User.builder()
                .publicUserId(UUID.randomUUID().toString())
                .birthday(userRegistrationDto.birthday())
                .email(userRegistrationDto.email())
                .name(userRegistrationDto.name())
                .lastname(userRegistrationDto.lastname())
                .passwordHash(passwordEncoder.encode(userRegistrationDto.password()))
                .build();
        return userMapper.toUserDetails(userRepository.save(newUser));
    }

    @Transactional
    public UserDetails updateUser(UserDetails userDetails, UserUpdateDto userUpdateDto) {
        var user = userRepository.findByPublicUserId(userDetails.publicUserId()).get();
        userMapper.updateUserFromUserUpdateDto(userUpdateDto, user);
        return userMapper.toUserDetails(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(UserDetails userDetails) {
        userRepository.delete(userRepository.findByPublicUserId(userDetails.publicUserId()).get());
    }

    @Transactional
    public UserDetails changePassword(UserDetails userDetails, ChangePasswordDto changePasswordDto) {
        var user = userRepository.findByPublicUserId(userDetails.publicUserId()).get();
        var updateUser = user.toBuilder()
                .passwordHash(passwordEncoder.encode(changePasswordDto.newPassword()))
                .build();
        return userMapper.toUserDetails(userRepository.save(updateUser));
    }

    public Optional<UserDetails> findByEmailIgnoreCase(UserRegistrationDto userRegistrationDto) {
        return userRepository.findByEmailIgnoreCase(userRegistrationDto.email()).map(userMapper::toUserDetails);
    }

    public Optional<UserDetails> findByPublicId(String publicUserId) {
        return userRepository.findByPublicUserId(publicUserId).map(userMapper::toUserDetails);
    }

    public Optional<String> findPasswordHashByPublicId(UserDetails userDetails) {
        return userRepository.findPasswordHashByPublicId(userDetails.publicUserId());
    }
}
