package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.dao.repository.CustomerStatisticRepository;
import ru.zinovev.online.store.dao.repository.ProductStatisticRepository;
import ru.zinovev.online.store.model.ProductShortDetails;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticDaoService {

    private final CustomerStatisticRepository customerStatisticRepository;
    private final ProductStatisticRepository productStatisticRepository;
    private final ProductMapper productMapper;

    public List<ProductShortDetails> findTopProducts(Pageable pageable) {
        return productStatisticRepository.findTopProductViews(pageable)
                .stream()
                .map(productMapper::toProductShortDetails)
                .toList();
    }
}
