package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.dao.mapper.OrderMapper;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.dao.mapper.UserMapper;
import ru.zinovev.online.store.dao.repository.CustomerStatisticRepository;
import ru.zinovev.online.store.dao.repository.ProductStatisticRepository;
import ru.zinovev.online.store.model.RevenueDetails;
import ru.zinovev.online.store.model.TopCustomerDetails;
import ru.zinovev.online.store.model.TopProductDetails;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticDaoService {

    private final CustomerStatisticRepository customerStatisticRepository;
    private final ProductStatisticRepository productStatisticRepository;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    public List<TopProductDetails> findTopProducts(Pageable pageable) {
        return productStatisticRepository.findTopProductViews(pageable)
                .stream()
                .map(productMapper::toTopProductDetails)
                .toList();
    }

    public List<TopCustomerDetails> findTopUsersByOrders(Pageable page, OffsetDateTime dateFrom,
                                                         OffsetDateTime dateTo) {
        return customerStatisticRepository.findTopCustomers(page, dateFrom, dateTo)
                .stream()
                .map(userMapper::toTopCustomerDetails)
                .toList();
    }

    public List<RevenueDetails> findRevenueByPeriod(Pageable page, OffsetDateTime dateFrom,
                                                    OffsetDateTime dateTo) {
        return customerStatisticRepository.findRevenueByPeriod(page, dateFrom, dateTo)
                .stream()
                .map(orderMapper::toRevenueDetails)
                .toList();
    }
}
