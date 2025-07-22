package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.controller.dto.OrderDto;
import ru.zinovev.online.store.dao.OrderDaoService;
import ru.zinovev.online.store.dao.entity.enums.OrderStatusName;
import ru.zinovev.online.store.dao.entity.enums.PaymentStatusName;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.OrderDetails;
import ru.zinovev.online.store.model.OrderShortDetails;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderDaoService orderDaoService;
    private final UserService userService;
    private final AddressService addressService;
    private final ProductService productService;

    public OrderDetails createOrder(@NonNull String publicUserId, @NonNull OrderDto orderDto) {
        var userDetails = userService.findUserDetails(publicUserId);
        var productDetails = productService.getByPublicId(orderDto.publicProductId());
        if (!addressService.existUserAddress(orderDto.publicAddressId(), publicUserId)) {
            throw new BadRequestException(
                    "Address with id - " + orderDto.publicAddressId() + " is not the address of the user with id - "
                            + publicUserId);
        }
        if (productDetails.stockQuantity() == 0) { //проверка здесь или на уровень ниже? в одной транзакции все учесть
            throw new BadRequestException(
                    "The product with id - " + orderDto.publicProductId() + " is out of stock");
        } else {
            productService.reserveProduct(productDetails.publicProductId());
        }
        return orderDaoService.createOrder(userDetails, productDetails, orderDto);
    }

    public List<OrderShortDetails> getUserOrders(String publicUserId) {
        userService.findUserDetails(publicUserId);
        return orderDaoService.getUserOrders(publicUserId);
    }

    public List<OrderShortDetails> getAllOrders(String publicUserId) {
        userService.findUserDetails(publicUserId);
        return orderDaoService.getAllOrders();
    }

    public OrderDetails changeOrderStatus(@NonNull String publicUserId, @NonNull String publicOrderId,
                                          @NonNull OrderStatusName orderStatusName,
                                          PaymentStatusName paymentStatusName) {
        userService.findUserDetails(publicUserId);
        var orderDetails = orderDaoService.findByPublicOrderId(publicOrderId)
                .orElseThrow(() -> new NotFoundException("Order with id - " + publicOrderId + " + not found"));
        if (!orderDetails.userDetails().publicUserId().equals(publicUserId)) {
            throw new BadRequestException(
                    "User with id - " + publicUserId + " did not create an order with id - " + publicOrderId);
        }
        return orderDaoService.changeOrderStatus(publicOrderId, orderStatusName, paymentStatusName);
    }
}
