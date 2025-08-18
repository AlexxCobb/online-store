package ru.zinovev.online.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.dao.StatisticDaoService;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.model.RevenueDetails;
import ru.zinovev.online.store.model.TopCustomerDetails;
import ru.zinovev.online.store.model.TopProductDetails;
import ru.zinovev.online.store.utils.PaginationServiceClass;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StatisticService {

    private final StatisticDaoService statisticDaoService;
    private final UserService userService;

    public List<TopProductDetails> getTopProducts(String publicUserId, Integer limit) {
        userService.findUserDetails(publicUserId);
        var page = PaginationServiceClass.pagination(0, limit);
        return statisticDaoService.findTopProducts(page);
    }

    public List<TopCustomerDetails> getTopUsersByOrders(String publicUserId, Integer limit, OffsetDateTime dateFrom,
                                                        OffsetDateTime dateTo) {
        userService.findUserDetails(publicUserId);
        var page = PaginationServiceClass.pagination(0, limit);
        checkPeriodOfTime(dateFrom, dateTo);
        return statisticDaoService.findTopUsersByOrders(page, dateFrom, dateTo);
    }

    public List<RevenueDetails> getStatistic(String publicUserId, Integer limit, OffsetDateTime dateFrom,
                                             OffsetDateTime dateTo) {
        userService.findUserDetails(publicUserId);
        var page = PaginationServiceClass.pagination(0, limit);
        checkPeriodOfTime(dateFrom, dateTo);
        return statisticDaoService.findRevenueByPeriod(page, dateFrom, dateTo);
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
