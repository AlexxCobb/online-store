package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.zinovev.online.store.dao.entity.Order;
import ru.zinovev.online.store.dao.entity.RevenueView;
import ru.zinovev.online.store.model.OrderDetails;
import ru.zinovev.online.store.model.OrderShortDetails;
import ru.zinovev.online.store.model.RevenueDetails;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "userDetails", source = "user")
    @Mapping(target = "paymentMethodName", source = "paymentMethod.name")
    @Mapping(target = "deliveryMethodName", source = "deliveryMethod.name")
    @Mapping(target = "paymentStatusName", source = "paymentStatus.name")
    @Mapping(target = "orderStatusName", source = "orderStatus.name")
    OrderDetails toOrderDetails(Order order);

    @Mapping(target = "userDetails", source = "user")
    @Mapping(target = "paymentStatusName", source = "paymentStatus.name")
    @Mapping(target = "deliveryMethodName", source = "deliveryMethod.name")
    @Mapping(target = "orderStatusName", source = "orderStatus.name")
    OrderShortDetails toOrderShortDetails(Order order);

    RevenueDetails toRevenueDetails(RevenueView revenueView);
}