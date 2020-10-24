package cn.zhang.mallmodified.queue;

import cn.hutool.core.util.StrUtil;
import cn.zhang.mallmodified.common.api.QueueEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author autum
 */
@Component
@Slf4j
public class CancelOrderSender {
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMessage(Long orderNo,final long delayTimes){
        //绑定exchange
        String exchange = QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange();
        //绑定routeKey
        String routeKey = QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey();
        amqpTemplate.convertAndSend(exchange,routeKey,orderNo, message -> {
            message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
            return message;
        });
        log.info("send cancel orderNo:"+orderNo);
    }
}
