package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.controller.dto.UserUpdateDto;
import ru.zinovev.online.store.dao.entity.User;
import ru.zinovev.online.store.dao.entity.enums.RoleName;
import ru.zinovev.online.store.dao.mapper.UserMapper;
import ru.zinovev.online.store.dao.repository.UserRepository;
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
    private final RoleDaoService roleDaoService;

    @Transactional
    public UserDetails createUser(UserRegistrationDetails userRegistrationDetails, String password) {
        var role = roleDaoService.findByName(RoleName.ROLE_USER);

        var newUser = User.builder()
                .publicUserId(UUID.randomUUID().toString())
                .birthday(userRegistrationDetails.birthday())
                .email(userRegistrationDetails.email())
                .name(userRegistrationDetails.name())
                .lastname(userRegistrationDetails.lastname())
                .passwordHash(password)
                .roles(Set.of(role))
                .build();
        return userMapper.toUserDetails(userRepository.save(newUser));
    }

    @Transactional
    public void updateUser(String publicUserId, UserUpdateDto userUpdateDto) {
        var updated = userRepository.updateNameAndLastnameAndEmailByPublicUserId(
                userUpdateDto.name(),
                userUpdateDto.lastname(),
                userUpdateDto.email(),
                publicUserId
        );
        if (updated == 0) {
            throw new NotFoundException("User with id - " + publicUserId + " not found");
        }
    }

    @Transactional
    public void deleteUser(String publicUserId) {
        int deleted = userRepository.deleteByPublicUserId(publicUserId);
        if (deleted == 0) {
            throw new NotFoundException("User with id - " + publicUserId + " not found");
        }
    }

    @Transactional
    public void changePassword(String publicUserId, String password) {
        userRepository.updatePasswordHashByPublicUserId(publicUserId, password);
    }

    public Optional<UserDetails> findByEmailIgnoreCase(String email) {
        return userRepository.findByEmailIgnoreCase(email).map(userMapper::toUserDetails);
    }

    public Optional<UserDetails> findByPublicId(String publicUserId) {
        var user = userRepository.findByPublicUserId(publicUserId);
        return user.map(userMapper::toUserDetails);
    }

    public boolean existByEmailIgnoreCase(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
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