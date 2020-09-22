package cn.zhang.mallmodified.queue;

import cn.zhang.mallmodified.dao.ProductDao;
import cn.zhang.mallmodified.dto.UpdateStockDto;
import cn.zhang.mallmodified.po.Product;
import cn.zhang.mallmodified.service.IProductService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author autum
 */
@Component
public class UpdateStockReceiver {
    @Autowired
    private ProductDao productDao;

    @RabbitHandler
    @RabbitListener(queues = "mall.product.updateStock")
    public void handle(UpdateStockDto updateStockDto){
        //更新锁定库存，只是对其他用户而言不可见
        Product product = productDao.selectByPrimaryKey(updateStockDto.getProductId());
        product.setLockStock(product.getLockStock()+updateStockDto.getStock());
        productDao.updateByPrimaryKeySelective(product);
    }
}
