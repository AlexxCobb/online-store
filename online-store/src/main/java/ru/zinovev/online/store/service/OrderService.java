package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.controller.dto.OrderDto;
import ru.zinovev.online.store.dao.OrderDaoService;
import ru.zinovev.online.store.dao.entity.enums.AddressTypeName;
import ru.zinovev.online.store.dao.entity.enums.DeliveryMethodName;
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
    private final CartService cartService;

    public OrderDetails createOrder(@NonNull String publicUserId, @NonNull OrderDto orderDto) {
        var userDetails = userService.findUserDetails(publicUserId);
        var cartDetails = cartService.getUserCart(publicUserId);
        checkDeliveryMethodWithAddress(publicUserId, orderDto.publicAddressId(), orderDto.deliveryMethodName());
        return orderDaoService.createOrder(userDetails, cartDetails, orderDto);
    }

    public List<OrderDetails> getUserOrders(String publicUserId) {
        userService.findUserDetails(publicUserId);
        return orderDaoService.getUserOrders(publicUserId);
    }

    public List<OrderShortDetails> getAllOrders(String publicUserId) {
        userService.findUserDetails(publicUserId);
        return orderDaoService.getAllOrders();
    }

    public OrderShortDetails getOrderById(String publicOrderId, String publicUserId) {
        userService.findUserDetails(publicUserId);
        return orderDaoService.findOrderById(publicOrderId)
                .orElseThrow(() -> new NotFoundException("Order with id - " + publicOrderId + " + not found"));
    }

    public void changeOrderStatus(@NonNull String publicUserId, @NonNull String publicOrderId,
                                  @NonNull OrderStatusName orderStatusName,
                                  PaymentStatusName paymentStatusName) {
        userService.findUserDetails(publicUserId);
        orderDaoService.changeOrderStatus(publicOrderId, orderStatusName, paymentStatusName);
    }

    private void checkDeliveryMethodWithAddress(String publicUserId, String publicAddressId, DeliveryMethodName name) {
        switch (name) {
            case COURIER -> {
                if (!addressService.existUserAddress(
                        publicAddressId, publicUserId)) {
                    throw new BadRequestException(
                            "Address with id - " + publicAddressId + " is not the address of the user with id - "
                                    + publicUserId);
                }
            }
            case PARCEL_LOCKER -> {
                if (!addressService.existSystemAddress(publicAddressId, AddressTypeName.PARCEL_LOCKER)) {
                    throw new BadRequestException(
                            "The selected address does not match the selected delivery method - "
                                    + name);
                }
            }
            case BY_SELF -> {
                if (!addressService.existSystemAddress(publicAddressId, AddressTypeName.STORE_ADDRESS)) {
                    throw new BadRequestException(
                            "The selected address does not match the selected delivery method - "
                                    + name);
                }
            }
            default -> throw new BadRequestException("Unknown delivery method");
        }
    }
}
