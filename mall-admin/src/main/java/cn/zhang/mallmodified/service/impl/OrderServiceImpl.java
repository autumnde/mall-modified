package cn.zhang.mallmodified.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhang.mallmodified.common.api.Const;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.dao.*;
import cn.zhang.mallmodified.po.*;
import cn.zhang.mallmodified.queue.CancelOrderSender;
import cn.zhang.mallmodified.queue.UpdateStockSender;
import cn.zhang.mallmodified.service.ICommonService;
import cn.zhang.mallmodified.service.IOrderService;
import cn.zhang.mallmodified.service.IRedisService;
import cn.zhang.mallmodified.vo.OrderItemVo;
import cn.zhang.mallmodified.vo.OrderProductVo;
import cn.zhang.mallmodified.vo.OrderVo;
import cn.zhang.mallmodified.vo.ShippingVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author autum
 */
@Service
@Slf4j
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private CartDao cartDao;
    @Autowired
    private ShippingDao shippingDao;
    @Autowired
    private ICommonService commonService;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private ProductDao productDao;
    @Value("${redis.key.product.stock}")
    private String REDIS_KEY_PRODUCT_STOCK;
    @Value("${ftp.server.host}")
    private String ImageHost;
    @Autowired
    private IRedisService redisService;
    @Autowired
    private CancelOrderSender cancelOrderSender;
    @Autowired
    private UpdateStockSender updateStockSender;
    @Autowired
    private RLock lock;

    @Override
    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        //根据用户id获取每个产品的订单信息
        List<OrderItem> orderItemList = (List<OrderItem>) commonService.assembleOrderItemList(userId).getData();
        //如果该用户对应购物车为空
        if(CollUtil.isEmpty(orderItemList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //判断商品库存是否够，足够的话减少可售库存数目（锁库存）
        //上分布式锁
        lock.lock();
        try{
            if(!hasStock(orderItemList)){
                //解锁
                lock.unlock();
                return ServerResponse.createByErrorMessage("非常抱歉，订单部分商品库存不足");
            }
        } finally {
            //解锁
            lock.unlock();
        }
        //根据用户id、购物车ip和总价格生成订单信息
        Order order = commonService.assembleOrder(userId,shippingId,getOrderProductTotalPrice(orderItemList));
        //各个OrderItem设定好订单编号
        for(OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //修改数据库,插入order表和orderItem表
        if(orderDao.insert(order) == 0){
            return ServerResponse.createByErrorMessage("订单创建失败");
        }
        orderItemDao.batchInsert(orderItemList);
        //延迟取消队列，时间到了就取消订单
        cancelOrderSender.sendMessage(order.getId().longValue(),3000);
        //生成订单后就清理购物车
        List<Cart> cartList = cartDao.selectByUserID(userId);
        cleanCart(cartList);
        //成功，返回订单详细信息
        OrderVo orderVo = commonService.assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    @Override
    public ServerResponse cancelOrder(long orderNo) {
        Order order = orderDao.selectByOrderNo(orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("此订单不存在");
        }
        if(order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()){
            return ServerResponse.createByErrorMessage("无法取消订单");
        }
        //获取所有的订单细则
        List<OrderItem> orderItemList = orderItemDao.selectByOrderNo(orderNo);
        //解锁订单锁住的库存
        for(OrderItem orderItem:orderItemList){
            Product product = productDao.selectByPrimaryKey(orderItem.getId());
            product.setLockStock(product.getStock()-orderItem.getQuantity());
            //数据库对应库存解锁
            productDao.updateByPrimaryKeySelective(product);
            //缓存库存回滚
            String strStock = redisService.get(REDIS_KEY_PRODUCT_STOCK+orderItem.getProductId());
            int newStock = Integer.parseInt(strStock) + orderItem.getQuantity();
            redisService.set(REDIS_KEY_PRODUCT_STOCK+orderItem.getProductId(),StrUtil.toString(newStock));
        }
        //修改订单状态
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());

        int row = orderDao.updateByPrimaryKeySelective(updateOrder);
        if(row > 0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse<OrderVo> getOrderDetail(Integer userId,Long orderNo){
        Order order = orderDao.selectByUserIdAndOrderNo(orderNo,userId);
        if(order != null){
            List<OrderItem> orderItemList = orderItemDao.getByOrderNoUserId(orderNo,userId);
            OrderVo orderVo = commonService.assembleOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return  ServerResponse.createByErrorMessage("没有找到该订单");
    }

    @Override
    public ServerResponse getOrderCartProduct(Integer userId){
        OrderProductVo orderProductVo = new OrderProductVo();
        //从购物车中获取数据
        List<Cart> cartList = cartDao.selectCheckedCartByUserId(userId);
        ServerResponse serverResponse =  this.getCartOrderItem(userId,cartList);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList =( List<OrderItem> ) serverResponse.getData();

        List<OrderItemVo> orderItemVoList = CollUtil.newArrayList();

        BigDecimal payment = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList){
            payment = NumberUtil.add(payment,orderItem.getTotalPrice());
            orderItemVoList.add(commonService.assembleOrderItemVo(orderItem));
        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(ImageHost);
        return ServerResponse.createBySuccess(orderProductVo);
    }

    @Override
    public ServerResponse<PageInfo> getOrderList(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderDao.selectByUserId(userId);
        List<OrderVo> orderVoList = commonService.assembleOrderVoList(orderList,userId);
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<PageInfo> manageList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderDao.selectAllOrder();
        List<OrderVo> orderVoList = commonService.assembleOrderVoList(orderList,null);
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 计算订单中所有商品的价格
     * @param orderItems
     * @return
     */
    public BigDecimal getOrderProductTotalPrice(List<OrderItem> orderItems) {
        BigDecimal totalPrice = new BigDecimal("0");
        for(OrderItem orderItem:orderItems){
            totalPrice = NumberUtil.add(totalPrice,orderItem.getCurrentUnitPrice());
        }
        return totalPrice;
    }

    @Override
    public long generateOrderNo(){
        long currentTime =System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }

    @Override
    public ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        Order order = orderDao.selectByOrderNo(orderNo);
        if(order != null){
            List<OrderItem> orderItemList = orderItemDao.selectByOrderNo(orderNo);
            OrderVo orderVo = commonService.assembleOrderVo(order,orderItemList);

            PageInfo pageResult = new PageInfo(CollUtil.newArrayList(order));
            pageResult.setList(CollUtil.newArrayList(orderVo));
            return ServerResponse.createBySuccess(pageResult);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    @Override
    public ServerResponse<String> manageSendGoods(Long orderNo){
        Order order= orderDao.selectByOrderNo(orderNo);
        if(order != null){
            if(order.getStatus() == Const.OrderStatusEnum.PAID.getCode()){
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                order.setSendTime(new Date());
                orderDao.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySuccess("发货成功");
            }
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }


    //辅助类方法
    /**
     * 生成订单后处理购物车
     * @param cartList
     */
    private void cleanCart(List<Cart> cartList){
        for(Cart cart : cartList){
            cartDao.deleteByPrimaryKey(cart.getId());
        }
    }

    /**
     * 判断订单各个商铺的库存是否充足
     * @param orderItemList
     * @return
     */
    private boolean hasStock(List<OrderItem> orderItemList){
        for(OrderItem orderItem : orderItemList){
            //查询缓存是否存在某个商品的库存
            String strStock = redisService.get(REDIS_KEY_PRODUCT_STOCK+orderItem.getProductId());
            if(strStock == null){
                //缓存未命中
                Product product = productDao.selectByPrimaryKey(orderItem.getProductId());
                //可售库存=总库存-锁定库存
                strStock = StrUtil.toString(product.getStock() - product.getLockStock());
                //加载新缓存
                redisService.set(REDIS_KEY_PRODUCT_STOCK+orderItem.getProductId(),strStock);
            }

            int stock = Integer.parseInt(strStock);
            if(stock < orderItem.getQuantity()){
                return false;
            }
        }
        //运行至此，说明库存充足,开始库存上锁
        for(OrderItem orderItem : orderItemList){
            //利用消息队列修改数据库,上锁库存
            updateStockSender.sendMessage(orderItem.getProductId(),orderItem.getQuantity());
            String strStock = redisService.get(REDIS_KEY_PRODUCT_STOCK+orderItem.getProductId());
            //缓存修改可售库存量
            int newStock = Integer.parseInt(strStock) - orderItem.getQuantity();
            redisService.set(REDIS_KEY_PRODUCT_STOCK+orderItem.getProductId(),StrUtil.toString(newStock));
        }
        return true;
    }

    private ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){
        List<OrderItem> orderItemList = CollUtil.newArrayList();
        if(CollUtil.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        //校验购物车的数据,包括产品的状态和数量
        for(Cart cartItem : cartList){
            OrderItem orderItem = new OrderItem();
            Product product = productDao.selectByPrimaryKey(cartItem.getProductId());
            if(Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()){
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"不是在线售卖状态");
            }

            //校验库存
            if(cartItem.getQuantity() > product.getStock()){
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"库存不足");
            }

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(NumberUtil.mul(product.getPrice(),cartItem.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }



}
