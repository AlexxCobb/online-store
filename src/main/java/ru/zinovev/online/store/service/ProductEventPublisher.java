package ru.zinovev.online.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.config.properties.RabbitMQProperties;
import ru.zinovev.online.store.controller.dto.ProductEventDto;

@Service
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final Queue productQueue;
    private final AmqpTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;

    public void send(ProductEventDto eventDto) {
        rabbitTemplate.convertAndSend(rabbitMQProperties.exchangeName(), productQueue.getName(), eventDto);
    }
}
