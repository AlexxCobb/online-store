package ru.zinovev.online.store.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.zinovev.online.store.config.properties.RabbitMQProperties;

@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
@RequiredArgsConstructor
public class RabbitConfig {

    private final RabbitMQProperties rabbitMQProperties;

    @Bean
    public Queue productQueue() {
        return QueueBuilder.durable(rabbitMQProperties.queueName())
                .withArgument("x-dead-letter-exchange", "product.dlx")
                .withArgument("x-dead-letter-routing-key", "product.dead")
                .build();
    }

    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(rabbitMQProperties.exchangeName());
    }

    @Bean
    public Binding binding(Queue productQueue, TopicExchange productExchange) {
        return BindingBuilder.bind(productQueue)
                .to(productExchange)
                .with(rabbitMQProperties.routingKey());
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("product.dlx");
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("product.dead.queue").build();
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("product.dead");
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
