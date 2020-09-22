package cn.zhang.mallmodified.queue;

import cn.zhang.mallmodified.common.api.QueueEnum;
import cn.zhang.mallmodified.dto.UpdateStockDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author autum
 */
@Component
@Slf4j
public class UpdateStockSender {
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMessage(Integer productId,int stock){
        UpdateStockDto updateStockDto = new UpdateStockDto(productId,stock);
        String exchangeDirect = QueueEnum.QUEUE_UPDATE_STOCK.getExchange();
        String routingKey = QueueEnum.QUEUE_UPDATE_STOCK.getRouteKey();
        amqpTemplate.convertAndSend(exchangeDirect,routingKey,updateStockDto);
    }
}
