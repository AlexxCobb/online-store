package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.controller.dto.OrderDto;
import ru.zinovev.online.store.dao.entity.Order;
import ru.zinovev.online.store.dao.entity.enums.OrderStatusName;
import ru.zinovev.online.store.dao.entity.enums.PaymentStatusName;
import ru.zinovev.online.store.dao.mapper.OrderMapper;
import ru.zinovev.online.store.dao.repository.AddressRepository;
import ru.zinovev.online.store.dao.repository.DeliveryMethodRepository;
import ru.zinovev.online.store.dao.repository.OrderRepository;
import ru.zinovev.online.store.dao.repository.OrderStatusRepository;
import ru.zinovev.online.store.dao.repository.PaymentMethodRepository;
import ru.zinovev.online.store.dao.repository.PaymentStatusRepository;
import ru.zinovev.online.store.dao.repository.ProductRepository;
import ru.zinovev.online.store.model.OrderDetails;
import ru.zinovev.online.store.model.OrderShortDetails;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.model.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderDaoService {

    private final OrderRepository orderRepository;
    private final UserDaoService userDaoService;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final DeliveryMethodRepository deliveryMethodRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDetails createOrder(UserDetails userDetails,
                                    ProductDetails productDetails,
                                    OrderDto orderDto) {
        var user = userDaoService.getByPublicId(userDetails.publicUserId());
        var address = addressRepository.findByPublicDeliveryAddressId(orderDto.publicAddressId())
                .get();
        var product = productRepository.findByPublicProductId(productDetails.publicProductId()).get();
        var payMethod = paymentMethodRepository.getByName(orderDto.paymentMethodName());
        var deliveryMethod = deliveryMethodRepository.getByName(orderDto.deliveryMethodName());
        var payStatus = paymentStatusRepository.getByName(PaymentStatusName.PENDING);
        var orderStatus = orderStatusRepository.getByName(OrderStatusName.PENDING_PAYMENT);

        var order = Order.builder()
                .publicOrderId(UUID.randomUUID().toString())
                .user(user)
                .address(address)
                .deliveryMethod(deliveryMethod)
                .paymentMethod(payMethod)
                .product(product)
                .paymentStatus(payStatus)
                .orderStatus(orderStatus)
                .build();
        return orderMapper.toOrderDetails(orderRepository.save(order));
    }

    public List<OrderShortDetails> getUserOrders(String publicUserId) {
        return orderRepository.findByUserPublicUserIdOrderByCreatedAtAsc(publicUserId)
                .stream()
                .map(orderMapper::toOrderShortDetails)
                .collect(Collectors.toList());
    }

    public List<OrderShortDetails> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toOrderShortDetails)
                .collect(Collectors.toList());
    }

    public Optional<OrderDetails> findByPublicOrderId(String publicOrderId) {
        return orderRepository.findByPublicOrderId(publicOrderId).map(orderMapper::toOrderDetails);
    }

    @Transactional
    public OrderDetails changeOrderStatus(String publicOrderId,
                                          OrderStatusName orderStatusName, PaymentStatusName paymentStatusName) {
        var order = orderRepository.findByPublicOrderId(publicOrderId).get();
        var orderStatus = orderStatusRepository.getByName(orderStatusName);
        Order updatedOrder;
        if (paymentStatusName == null) {
            updatedOrder = order.toBuilder().orderStatus(orderStatus).build();
        } else {
            var payStatus = paymentStatusRepository.getByName(paymentStatusName);
            updatedOrder = order.toBuilder().orderStatus(orderStatus).paymentStatus(payStatus).build();
        }
        return orderMapper.toOrderDetails(orderRepository.save(updatedOrder));
    }
}
