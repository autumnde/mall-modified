package cn.zhang.mallmodified.queue;

import cn.zhang.mallmodified.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author autum
 */
@Component

@Slf4j
public class CancelOrderReceiver {
    @Autowired
    private IOrderService orderService;

    @RabbitHandler
    @RabbitListener(queues = "mall.order.cancel")
    public void handle(long orderNo){
        log.info("receive cancel orderNo");
        orderService.cancelOrder(orderNo);
    }
}
