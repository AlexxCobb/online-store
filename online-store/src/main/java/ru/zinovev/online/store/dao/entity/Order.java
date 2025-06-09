package ru.zinovev.online.store.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import ru.zinovev.online.store.dao.entity.enums.DeliveryMethod;
import ru.zinovev.online.store.dao.entity.enums.DeliveryStatus;
import ru.zinovev.online.store.dao.entity.enums.PaymentMethod;
import ru.zinovev.online.store.dao.entity.enums.PaymentStatus;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress address;

    @Enumerated(value = EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(value = EnumType.STRING)
    private DeliveryMethod deliveryMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(value = EnumType.STRING)
    private DeliveryStatus deliveryStatus;
}