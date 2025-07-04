package ru.zinovev.online.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.zinovev.online.store.controller.dto.OrderDto;
import ru.zinovev.online.store.dao.entity.enums.OrderStatusName;
import ru.zinovev.online.store.dao.entity.enums.PaymentStatusName;
import ru.zinovev.online.store.model.OrderDetails;
import ru.zinovev.online.store.model.OrderShortDetails;
import ru.zinovev.online.store.service.OrderService;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{publicUserId}")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDetails addOrder(@PathVariable String publicUserId, @Valid @RequestBody OrderDto orderDto) {
        log.debug("Received POST request to add order, dto - {}, from user - {}", orderDto, publicUserId);
        return orderService.createOrder(publicUserId, orderDto);
    }

    @GetMapping("/{publicUserId}")
    public List<OrderShortDetails> getOrders(@PathVariable String publicUserId) {
        log.debug("Received GET request to get user orders with userId - {}", publicUserId);
        return orderService.getUserOrders(publicUserId);
    }

    @PatchMapping("/{publicUserId}/status/{publicOrderId}")
    public OrderDetails changeOrderStatus(@PathVariable String publicUserId, @PathVariable String publicOrderId,
                                          @RequestParam OrderStatusName orderStatusName,
                                          @RequestParam(required = false) PaymentStatusName paymentStatusName) {
        log.debug(
                "Received PATCH request to change order status, with id - {}, orderStatusName - {}, paymentStatusName -{}",
                publicOrderId, orderStatusName, paymentStatusName);
        return orderService.changeOrderStatus(publicOrderId, publicUserId, orderStatusName, paymentStatusName);
    }

    //просмотр заказов админом, управление статусами заказов

}
