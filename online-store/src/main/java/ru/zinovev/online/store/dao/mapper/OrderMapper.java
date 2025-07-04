package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.Mapper;
import ru.zinovev.online.store.dao.entity.Order;
import ru.zinovev.online.store.model.OrderDetails;
import ru.zinovev.online.store.model.OrderShortDetails;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDetails toOrderDetails(Order order);

    OrderShortDetails toOrderShortDetails(Order order);

}
