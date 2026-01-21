package ru.zinovev.online.store.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.zinovev.online.store.dao.entity.enums.RoleName;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "\"role\"")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    private RoleName name;

    @Column(name = "role_description")
    private String description;

    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    @JsonIgnore
    private Set<User> users = new HashSet<>();
}