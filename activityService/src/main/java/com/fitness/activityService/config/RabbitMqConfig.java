package com.fitness.activityService.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;

@Configuration
public class RabbitMqConfig {
    @Value("${rabbitmq.queue.name}")
    private String queueName;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;


    @Bean
    public Queue activityQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public DirectExchange activityExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding activityBinding(Queue activityQueue, DirectExchange activityExchange) {
        return BindingBuilder.bind(activityQueue).to(activityExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMsgConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
