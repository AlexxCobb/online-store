package ru.zinovev.online.store.config;

import lombok.Setter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_PRODUCT_EVENTS = "product.events.exchange";

    @Value("${queue.name}")
    private String queueName;

    @Bean
    public Queue productQueue() {
        return new Queue(queueName, true);
    }


    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(EXCHANGE_PRODUCT_EVENTS);
    }

    @Bean
    public Binding binding(Queue productQueue, TopicExchange productExchange) {
        return BindingBuilder.bind(productQueue)
                .to(productExchange)
                .with(queueName);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationRunner runner(RabbitAdmin admin, Queue productQueue) {
        return args -> admin.declareQueue(productQueue);
    }
}
