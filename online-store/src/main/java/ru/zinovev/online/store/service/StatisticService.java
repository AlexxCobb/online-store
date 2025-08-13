package ru.zinovev.online.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.dao.StatisticDaoService;
import ru.zinovev.online.store.model.ProductShortDetails;
import ru.zinovev.online.store.utils.PaginationServiceClass;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StatisticService {

    private final StatisticDaoService statisticDaoService;
    private final UserService userService;

    public List<ProductShortDetails> getTopProducts(String publicUserId, Integer limit) {
        userService.findUserDetails(publicUserId);
        var page = PaginationServiceClass.pagination(0, limit);
        return statisticDaoService.findTopProducts(page);
    }

}
