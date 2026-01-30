package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.dao.entity.CustomerStatistic;
import ru.zinovev.online.store.dao.entity.Order;
import ru.zinovev.online.store.dao.entity.ProductStatistic;
import ru.zinovev.online.store.dao.mapper.OrderMapper;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.dao.mapper.UserMapper;
import ru.zinovev.online.store.dao.repository.CustomerStatisticRepository;
import ru.zinovev.online.store.dao.repository.ProductStatisticRepository;
import ru.zinovev.online.store.model.RevenueDetails;
import ru.zinovev.online.store.model.TopCustomerDetails;
import ru.zinovev.online.store.model.TopProductDetails;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticDaoService {

    private final CustomerStatisticRepository customerStatisticRepository;
    private final ProductStatisticRepository productStatisticRepository;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void createStatistic(Order order) {
        var totalSpent = order.getItems().stream().map(orderItem -> orderItem.getPriceAtPurchase().multiply(
                BigDecimal.valueOf(orderItem.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        var topProducts = order.getItems()
                .stream()
                .map(orderItem -> ProductStatistic.builder()
                        .order(order)
                        .product(orderItem.getProduct())
                        .purchaseCount(orderItem.getQuantity())
                        .build())
                .toList();
        var customerStat =
                CustomerStatistic.builder()
                        .user(order.getUser())
                        .order(order)
                        .totalSpent(totalSpent)
                        .build();
        productStatisticRepository.saveAll(topProducts);
        customerStatisticRepository.save(customerStat);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void cancelStatistic(Order order) {
        productStatisticRepository.deleteByOrder(order);
        customerStatisticRepository.deleteByOrder(order);
    }

    public Page<TopProductDetails> findTopProducts(Pageable pageable) {
        var topProductViews = productStatisticRepository.findTopProductViews(pageable);
        var topDetails = topProductViews.stream().map(productMapper::toTopProductDetails).toList();
        return new PageImpl<>(topDetails, pageable, topProductViews.getTotalElements());
    }

    public Page<TopCustomerDetails> findTopUsersByOrders(Pageable pageable, OffsetDateTime dateFrom,
                                                         OffsetDateTime dateTo) {
        var topUserViews = customerStatisticRepository.findTopCustomers(pageable, dateFrom, dateTo);
        var topDetails = topUserViews.stream().map(userMapper::toTopCustomerDetails).toList();
        return new PageImpl<>(topDetails, pageable, topUserViews.getTotalElements());
    }

    public Page<RevenueDetails> findRevenueByPeriod(Pageable pageable, OffsetDateTime dateFrom,
                                                    OffsetDateTime dateTo) {
        var topRevenue = customerStatisticRepository.findRevenueByPeriod(pageable, dateFrom, dateTo);
        var topDetails = topRevenue.stream().map(orderMapper::toRevenueDetails).toList();
        return new PageImpl<>(topDetails, pageable, topRevenue.getTotalElements());
    }
}
