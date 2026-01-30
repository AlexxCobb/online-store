package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.config.cache.Caches;
import ru.zinovev.online.store.dao.entity.Role;
import ru.zinovev.online.store.dao.entity.enums.RoleName;
import ru.zinovev.online.store.dao.repository.RoleRepository;
import ru.zinovev.online.store.exception.model.NotFoundException;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleDaoService {

    private final RoleRepository roleRepository;

    @Cacheable(value = Caches.Role.BY_NAME, key = "#name")
    public Role findByName(RoleName name) {
        return roleRepository.findByName(name).orElseThrow(() -> new NotFoundException("Role USER not found"));
    }
}
