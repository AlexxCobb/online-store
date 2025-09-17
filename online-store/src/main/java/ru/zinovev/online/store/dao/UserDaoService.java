package ru.zinovev.online.store.dao;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.controller.dto.ChangePasswordDto;
import ru.zinovev.online.store.controller.dto.UserUpdateDto;
import ru.zinovev.online.store.dao.entity.User;
import ru.zinovev.online.store.dao.entity.enums.RoleName;
import ru.zinovev.online.store.dao.mapper.UserMapper;
import ru.zinovev.online.store.dao.repository.UserRepository;
import ru.zinovev.online.store.exception.model.ForbiddenException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.UserDetails;
import ru.zinovev.online.store.model.UserRegistrationDetails;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDaoService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleDaoService roleDaoService;

    @Transactional
    public UserDetails createUser(UserRegistrationDetails userRegistrationDetails) {
        var role = roleDaoService.findByName(RoleName.ROLE_USER);

        var newUser = User.builder()
                .publicUserId(UUID.randomUUID().toString())
                .birthday(userRegistrationDetails.birthday())
                .email(userRegistrationDetails.email())
                .name(userRegistrationDetails.name())
                .lastname(userRegistrationDetails.lastname())
                .passwordHash(passwordEncoder.encode(userRegistrationDetails.password()))
                .roles(Set.of(role))
                .build();
        return userMapper.toUserDetails(userRepository.save(newUser));
    }

    public UserDetails singIn(@NonNull String email, @NonNull String password) {
        var user = userRepository.findByEmailIgnoreCase(email);
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPasswordHash())) {
            throw new ForbiddenException("User with email: %s not found or password is incorrect".formatted(email));
        }
        return userMapper.toUserDetails(user.get());
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

    public Optional<UserDetails> findByEmailIgnoreCase(String email) {
        return userRepository.findByEmailIgnoreCase(email).map(userMapper::toUserDetails);
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

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email - %s not found".formatted(email)));
        var rolesWithoutPrefix = user.getRoles().stream()
                .map(role -> {
                    String roleName = role.getName().name();
                    if (roleName.startsWith("ROLE_")) {
                        return roleName.substring("ROLE_".length()); //костыль, исправить
                    }
                    return roleName;
                })
                .toArray(String[]::new);
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles(rolesWithoutPrefix)
                .build();
    }
}
