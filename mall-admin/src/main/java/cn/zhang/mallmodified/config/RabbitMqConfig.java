package cn.zhang.mallmodified.config;

import cn.zhang.mallmodified.common.api.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author autum
 */
@Configuration
public class RabbitMqConfig {
    /**
     * 订单消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange orderDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 订单延迟队列队列所绑定的交换机
     */
    @Bean
    DirectExchange orderTtlDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 更新库存队列所绑定的交换机
     * @return
     */
    @Bean
    DirectExchange updateStockDirect(){
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_UPDATE_STOCK.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 订单实际消费队列
     */
    @Bean
    public Queue orderQueue() {
        return new Queue(QueueEnum.QUEUE_ORDER_CANCEL.getName());
    }

    /**
     * 订单延迟队列（死信队列）
     */
    @Bean
    public Queue orderTtlQueue() {
        return QueueBuilder
                .durable(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_ORDER_CANCEL.getExchange())//到期后转发的交换机
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey())//到期后转发的路由键
                .build();
    }

    /**
     * 更新库存队列
     * @return
     */
    @Bean
    public Queue updateStockQueue(){
        return new Queue(QueueEnum.QUEUE_UPDATE_STOCK.getName());
    }

    /**
     * 将订单队列绑定到交换机
     */
    @Bean
    Binding orderBinding(DirectExchange orderDirect, Queue orderQueue){
        return BindingBuilder
                .bind(orderQueue)
                .to(orderDirect)
                .with(QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey());
    }

    /**
     * 将订单延迟队列绑定到交换机
     */
    @Bean
    Binding orderTtlBinding(DirectExchange orderTtlDirect,Queue orderTtlQueue){
        return BindingBuilder
                .bind(orderTtlQueue)
                .to(orderTtlDirect)
                .with(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey());
    }

    /**
     * 将更新商品库存队列绑定到交换机
     * @param updateStockDirect
     * @param updateStockQueue
     * @return
     */
    @Bean
    Binding updateStockBinding(DirectExchange updateStockDirect,Queue updateStockQueue){
        return BindingBuilder
                .bind(updateStockQueue)
                .to(updateStockDirect)
                .with(QueueEnum.QUEUE_UPDATE_STOCK.getRouteKey());
    }
}
