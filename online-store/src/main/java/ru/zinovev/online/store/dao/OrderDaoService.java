package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
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
import ru.zinovev.online.store.dao.repository.ProductRepository;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.exception.model.OutOfStockException;
import ru.zinovev.online.store.model.CartDetails;
import ru.zinovev.online.store.model.OrderDetails;
import ru.zinovev.online.store.model.OrderShortDetails;
import ru.zinovev.online.store.model.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final ProductRepository productRepository;
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
        var productsList = updateProductsQuantity(products);
        var productMap = productsList.stream()
                .collect(Collectors.toMap(Product::getPublicProductId, Function.identity()));

        var items = cart.getItems().stream().map(cartItem -> {
            var product = productMap.get(cartItem.getProduct().getPublicProductId());
            return OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .product(product)
                    .build();
        }).toList();

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
        var updatedItems = items.stream().map(orderItem -> orderItem.toBuilder().order(order).build()).toList();
        var orderWithItems = order.toBuilder().items(updatedItems).build();

        return orderMapper.toOrderDetails(orderRepository.save(orderWithItems));
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

    @Transactional
    public OrderDetails changeOrderStatus(String publicOrderId,
                                          OrderStatusName orderStatusName, PaymentStatusName paymentStatusName) {
        var order = orderRepository.findByPublicOrderId(publicOrderId)
                .orElseThrow(() -> new NotFoundException("Order with id - " + publicOrderId + " + not found"));
        var orderStatus = orderStatusRepository.getByName(orderStatusName);
        var updatedOrder = order.toBuilder().orderStatus(orderStatus);

        if (paymentStatusName != null) {
            var payStatus = paymentStatusRepository.getByName(paymentStatusName);
            updatedOrder.paymentStatus(payStatus);
        }
        var savedOrder = orderRepository.save(updatedOrder.build());
        if (orderStatusName.equals(OrderStatusName.DELIVERED) && !OrderStatusName.DELIVERED.equals(
                order.getOrderStatus().getName())) {
            statisticDaoService.createStatistic(savedOrder);
        }
        //реализовать логику при отмене/возврате заказа
        return orderMapper.toOrderDetails(savedOrder);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    private List<Product> updateProductsQuantity(Map<Product, Integer> products) {
        var productsList = new ArrayList<Product>();
        products.forEach((product, integer) -> {
            if (product.getStockQuantity() < integer) {
                throw new OutOfStockException(
                        "You cannot order the selected quantity - " + integer + " of product with id - "
                                + product.getPublicProductId() + ", the remainder in the warehouse is - "
                                + product.getStockQuantity());
            }
            var productToUpdate = product.toBuilder()
                    .stockQuantity(product.getStockQuantity() - integer)
                    .build();
            productsList.add(productToUpdate);
        });
        return productRepository.saveAll(productsList);
    }
}
