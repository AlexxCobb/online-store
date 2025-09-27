package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.controller.dto.OrderDto;
import ru.zinovev.online.store.dao.entity.CartItem;
import ru.zinovev.online.store.dao.entity.Order;
import ru.zinovev.online.store.dao.entity.OrderItem;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.dao.entity.enums.OrderStatusName;
import ru.zinovev.online.store.dao.entity.enums.PaymentStatusName;
import ru.zinovev.online.store.dao.mapper.OrderMapper;
import ru.zinovev.online.store.dao.repository.AddressRepository;
import ru.zinovev.online.store.dao.repository.CartRepository;
import ru.zinovev.online.store.dao.repository.DeliveryMethodRepository;
import ru.zinovev.online.store.dao.repository.OrderRepository;
import ru.zinovev.online.store.dao.repository.OrderStatusRepository;
import ru.zinovev.online.store.dao.repository.PaymentMethodRepository;
import ru.zinovev.online.store.dao.repository.PaymentStatusRepository;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.CartDetails;
import ru.zinovev.online.store.model.OrderDetails;
import ru.zinovev.online.store.model.OrderShortDetails;
import ru.zinovev.online.store.model.UserDetails;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderDaoService {

    private final OrderRepository orderRepository;
    private final UserDaoService userDaoService;
    private final StatisticDaoService statisticDaoService;
    private final CartRepository cartRepository;
    private final ProductDaoService productDaoService;
    private final AddressRepository addressRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final DeliveryMethodRepository deliveryMethodRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDetails createOrder(UserDetails userDetails,
                                    CartDetails cartDetails,
                                    OrderDto orderDto) {
        var user = userDaoService.getByPublicId(userDetails.publicUserId());
        var address = addressRepository.findByPublicDeliveryAddressId(orderDto.publicAddressId())
                .orElseThrow(() -> new NotFoundException(
                        "Address with id - " + orderDto.publicAddressId() + " + not found"));
        var cart = cartRepository.findByPublicCartId(cartDetails.publicCartId())
                .orElseThrow(
                        () -> new NotFoundException("Cart with id - " + cartDetails.publicCartId() + " not found"));
        var products = cart.getItems().stream().collect(Collectors.toMap(CartItem::getProduct, CartItem::getQuantity));
        var productsList = productDaoService.updateProductsQuantity(products);
        var productMap = productsList.stream()
                .collect(Collectors.toMap(Product::getPublicProductId, Function.identity()));
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
                .paymentStatus(payStatus)
                .orderStatus(orderStatus)
                .build();
        var items = cart.getItems().stream().map(cartItem -> {
            var product = productMap.get(cartItem.getProduct().getPublicProductId());
            return OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .product(product)
                    .order(order)
                    .build();
        }).toList();

        //   var updatedItems = items.stream().map(orderItem -> orderItem.toBuilder().order(order).build()).toList();
        order.getItems().addAll(items);
        var newOrd = orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);

        return orderMapper.toOrderDetails(newOrd);
    }

    public Page<OrderDetails> getUserOrders(String publicUserId, Integer page, Integer limit) {
        var pageable = PageRequest.of(page, limit);

        var orders = orderRepository.findByUserPublicUserIdOrderByCreatedAtDesc(publicUserId, pageable);
        var orderDetails = orders
                .stream()
                .map(orderMapper::toOrderDetails)
                .toList();
        return new PageImpl<>(orderDetails, pageable, orders.getTotalElements());
    }

    public Page<OrderShortDetails> getAllOrders(Integer page, Integer limit) {
        var sort = Sort.by(Sort.Direction.DESC, "createdAt");
        var pageable = PageRequest.of(page, limit, sort);

        var orders = orderRepository.findAll(pageable);
        var orderDetails = orders
                .stream()
                .map(orderMapper::toOrderShortDetails)
                .toList();
        return new PageImpl<>(orderDetails, pageable, orders.getTotalElements());
    }

    public Optional<OrderShortDetails> findOrderById(String publicOrderId) {
        return orderRepository.findByPublicOrderId(publicOrderId).map(orderMapper::toOrderShortDetails);
    }


    @Transactional
    public void changeOrderStatus(String publicOrderId,
                                  OrderStatusName orderStatusName, PaymentStatusName paymentStatusName) {
        var order = orderRepository.findByPublicOrderId(publicOrderId)
                .orElseThrow(() -> new NotFoundException("Order with id - " + publicOrderId + " + not found"));
        var existedOrderStatus = order.getOrderStatus().getName();
        if (existedOrderStatus.equals(OrderStatusName.DELIVERED) && !orderStatusName.equals(
                OrderStatusName.CANCELLED)) {
            throw new BadRequestException("The DELIVERED status can only be changed to CANCELLED");
        }
        if (existedOrderStatus.equals(OrderStatusName.CANCELLED)) {
            throw new BadRequestException("The CANCELLED status can not be changed");
        }
        var orderStatus = orderStatusRepository.getByName(orderStatusName);
        var updatedOrder = order.toBuilder().orderStatus(orderStatus);

        if (paymentStatusName != null) {
            var payStatus = paymentStatusRepository.getByName(paymentStatusName);
            updatedOrder.paymentStatus(payStatus);
        }
        var savedOrder = orderRepository.save(updatedOrder.build());
        if (orderStatusName.equals(OrderStatusName.DELIVERED)) {
            statisticDaoService.createStatistic(savedOrder);
        }
        if (orderStatusName.equals(OrderStatusName.CANCELLED)) {
            statisticDaoService.cancelStatistic(savedOrder);
            productDaoService.returnProductsToWarehouse(order.getItems());
        }
    }
}