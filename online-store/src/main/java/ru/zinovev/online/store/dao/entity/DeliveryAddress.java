package ru.zinovev.online.store.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_type_id")
    private AddressType addressType;

    private String country;
    private String town;
    @Setter
    @Column(name = "zip_code")
    private Integer zipCode;
    @Setter
    private String street;
    @Setter
    @Column(name = "house_number")
    private Integer houseNumber;
    @Setter // костыль, но по другому не генерит обновление из деталей в сущность MapStruct, подумать
    @Column(name = "flat_number")
    private Integer flatNumber;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "is_system")
    private Boolean isSystem;

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof DeliveryAddress deliveryAddress))
            return false;
        return id != null && id.equals(deliveryAddress.id);
    }
}