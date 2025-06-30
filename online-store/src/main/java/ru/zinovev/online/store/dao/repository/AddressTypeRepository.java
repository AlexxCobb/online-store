package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.AddressType;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;

public interface AddressTypeRepository extends JpaRepository<AddressType, Integer> {

    AddressType getByName(AddressTypeName name);
}
