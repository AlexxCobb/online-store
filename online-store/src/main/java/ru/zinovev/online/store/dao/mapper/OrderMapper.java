package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.zinovev.online.store.dao.entity.Order;
import ru.zinovev.online.store.dao.entity.OrderItem;
import ru.zinovev.online.store.dao.entity.RevenueView;
import ru.zinovev.online.store.model.OrderDetails;
import ru.zinovev.online.store.model.OrderShortDetails;
import ru.zinovev.online.store.model.ProductShortDetails;
import ru.zinovev.online.store.model.RevenueDetails;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "userDetails", source = "user")
    @Mapping(target = "paymentMethodName", source = "paymentMethod.name")
    @Mapping(target = "deliveryMethodName", source = "deliveryMethod.name")
    @Mapping(target = "paymentStatusName", source = "paymentStatus.name")
    @Mapping(target = "orderStatusName", source = "orderStatus.name")
    @Mapping(target = "productShortDetails", source = "items")
    @Mapping(target = "addressShortDetails.town", source = "address.town")
    @Mapping(target = "addressShortDetails.street", source = "address.street")
    @Mapping(target = "addressShortDetails.houseNumber", source = "address.houseNumber")
    @Mapping(target = "addressShortDetails.flatNumber", source = "address.flatNumber")
    @Mapping(target = "createdAt", expression = "java(order.getCreatedAt().atZoneSameInstant(java.time.ZoneId.systemDefault()).toOffsetDateTime())")
    OrderDetails toOrderDetails(Order order);


    @Mapping(target = "publicProductId", source = "product.publicProductId")
    @Mapping(target = "name", source = "product.name")
    ProductShortDetails toProductShortDetails(OrderItem orderItem);

    @Mapping(target = "userDetails", source = "user")
    @Mapping(target = "paymentStatusName", source = "paymentStatus.name")
    @Mapping(target = "deliveryMethodName", source = "deliveryMethod.name")
    @Mapping(target = "orderStatusName", source = "orderStatus.name")
    @Mapping(target = "createdAt", expression = "java(order.getCreatedAt().atZoneSameInstant(java.time.ZoneId.systemDefault()).toOffsetDateTime())")
    OrderShortDetails toOrderShortDetails(Order order);

    @Mapping(target = "createdAt", expression = "java(revenueView.getCreatedAt().atZoneSameInstant(java.time.ZoneId.systemDefault()).toOffsetDateTime())")
    RevenueDetails toRevenueDetails(RevenueView revenueView);
}