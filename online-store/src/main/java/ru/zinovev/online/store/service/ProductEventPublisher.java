package ru.zinovev.online.store.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.config.RabbitConfig;
import ru.zinovev.online.store.controller.dto.ProductEventDto;
import ru.zinovev.online.store.controller.dto.ProductForStandDto;
import ru.zinovev.online.store.controller.dto.enums.EventType;

@Setter
@Service
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final Queue productQueue;
    private final AmqpTemplate rabbitTemplate;

    public void publishProductUpdateEvent(ProductForStandDto productForStandDto) {
        var event = new ProductEventDto(EventType.UPDATE, productForStandDto.publicProductId(), productForStandDto,
                                        System.currentTimeMillis());

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_PRODUCT_EVENTS, productQueue.getName(), event);
    }

    public void publishProductDeleteEvent(ProductForStandDto productForStandDto) {
        var event = new ProductEventDto(EventType.DELETE, productForStandDto.publicProductId(), null,
                                        System.currentTimeMillis());

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_PRODUCT_EVENTS, productQueue.getName(), event);
    }
}
