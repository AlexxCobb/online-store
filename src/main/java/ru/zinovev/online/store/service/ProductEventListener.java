package ru.zinovev.online.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.zinovev.online.store.controller.dto.ProductEventDto;

@Service
@RequiredArgsConstructor
public class ProductEventListener {

    private final ProductEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(ProductEventDto eventDto) {
        eventPublisher.send(eventDto);
    }
}
