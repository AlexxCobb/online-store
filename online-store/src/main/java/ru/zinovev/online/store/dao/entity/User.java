package ru.zinovev.online.store.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_public_id")
    private String publicUserId;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_lastname")
    private String lastname;

    @Column(name = "user_birthday")
    private LocalDate birthday;

    @Column(name = "user_email")
    private String email;

    @Column(name = "user_password_hash")
    private String passwordHash;
}
