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

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "delivery_address")
public class DeliveryAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_address_id")
    private Long id;
    @Column(name = "delivery_address_public_id")
    private String publicDeliveryAddressId;
    private String country;
    private String town;
    @Column(name = "zip_code")
    private Integer zipCode;
    private String street;
    @Column(name = "house_number")
    private Integer houseNumber;
    @Column(name = "flat_number")
    private Integer flatNumber;
}