package ru.zinovev.online.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.dao.StatisticDaoService;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.model.RevenueDetails;
import ru.zinovev.online.store.model.TopCustomerDetails;
import ru.zinovev.online.store.model.TopProductDetails;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Service
public class StatisticService {

    private final StatisticDaoService statisticDaoService;
    private final UserService userService;

    public Page<TopProductDetails> getTopProducts(String publicUserId, Pageable pageable) {
        userService.findUserDetails(publicUserId);
        return statisticDaoService.findTopProducts(pageable);
    }

    public Page<TopCustomerDetails> getTopUsersByOrders(String publicUserId, Pageable pageable, OffsetDateTime dateFrom,
                                                        OffsetDateTime dateTo) {
        userService.findUserDetails(publicUserId);
        checkPeriodOfTime(dateFrom, dateTo);
        return statisticDaoService.findTopUsersByOrders(pageable, dateFrom, dateTo);
    }

    public Page<RevenueDetails> getStatistic(String publicUserId, Pageable pageable, OffsetDateTime dateFrom,
                                             OffsetDateTime dateTo) {
        userService.findUserDetails(publicUserId);
        checkPeriodOfTime(dateFrom, dateTo);
        return statisticDaoService.findRevenueByPeriod(pageable, dateFrom, dateTo);
    }

    public BigDecimal getTotalRevenue(Page<RevenueDetails> revenueDetails) {
        if (revenueDetails.hasContent()) {
            return revenueDetails.getContent()
                    .stream()
                    .map(RevenueDetails::totalOrderSpent)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }

    private void checkPeriodOfTime(OffsetDateTime dateFrom, OffsetDateTime dateTo) {
        if (dateFrom.isAfter(dateTo)) {
            throw new BadRequestException(
                    "The start date (" + dateFrom + ") cannot be later than the end date (" + dateTo + ")");
        }
        if (dateFrom.isAfter(OffsetDateTime.now())) {
            throw new BadRequestException("The start date (" + dateFrom + ") cannot be in the future");
        }
        if (dateTo.isAfter(OffsetDateTime.now())) {
            throw new BadRequestException("End date (" + dateTo + ") cannot be in the future");
        }
    }
}
