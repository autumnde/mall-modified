package cn.zhang.mallmodified.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.zhang.mallmodified.common.api.Const;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.dao.*;
import cn.zhang.mallmodified.po.*;
import cn.zhang.mallmodified.service.ICommonService;
import cn.zhang.mallmodified.service.IOrderService;
import cn.zhang.mallmodified.service.IUserService;
import cn.zhang.mallmodified.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author autum
 */
@Service
public class CommonServiceImpl implements ICommonService {
    @Autowired
    private IUserService userService;
    @Autowired
    private CartDao cartDao;
    @Value("${ftp.server.host}")
    private String ImageHost;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ShippingDao shippingDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private CategoryDao categoryDao;



    @Override
    public ServerResponse AdminJudge(HttpSession httpSession) {
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        if(!userService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("无管理员权限");
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse assembleOrderItemList(Integer userId) {
        List<Cart> cartList = cartDao.selectCheckedCartByUserId(userId);
        List<OrderItem> orderItemList = new ArrayList<>();
        if(cartList.size() == 0){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //开始cartList转化为OrderItemList
        for(Cart cart:cartList){
            OrderItem orderItem = new OrderItem();
            //获取购物车内商品信息
            Product product = productDao.selectByPrimaryKey(cart.getProductId());
            //检验商品是否可卖
            if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
                return ServerResponse.createByErrorMessage("商品"+product.getName()+"无法出售");
            }

            //填补订单细则信息
            orderItem.setUserId(userId);
            orderItem.setProductName(product.getName());
            orderItem.setTotalPrice(NumberUtil.mul(cart.getQuantity(),product.getPrice()));
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setCreateTime(DateUtil.date());
            orderItem.setProductId(cart.getProductId());
            orderItem.setCurrentUnitPrice(product.getPrice());

            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    @Override
    public boolean isAllChecked(Integer userId){
        if(userId == null){
            return false;
        }
        return cartDao.selectCheckedNumOf(userId) == 0;
    }

    @Override
    public CartVo assembleCartVo(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartDao.selectByUserID(userId);
        List<CartProductVo> cartProductVoList = new ArrayList<>();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if(CollUtil.isNotEmpty(cartList)){
            for(Cart cartItem : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productDao.selectByPrimaryKey(cartItem.getProductId());
                if(product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cartItem.getQuantity()){
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartDao.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(NumberUtil.mul(product.getPrice(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                if(cartItem.getChecked() == Const.Cart.CHECKED){
                    //如果已经勾选,增加到整个的购物车总价中
                    cartTotalPrice = NumberUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.isAllChecked(userId));
        cartVo.setImageHost(ImageHost);

        return cartVo;
    }

    @Override
    public Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment){
        Order order = new Order();
        //生成订单编号
        long orderNo = orderService.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);

        order.setUserId(userId);
        order.setShippingId(shippingId);

        int rowCount = orderDao.insert(order);
        if(rowCount > 0){
            return order;
        }
        return null;
    }

    @Override
    public OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingDao.selectByPrimaryKey(order.getShippingId());
        if(shipping != null){
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        orderVo.setPaymentTime(DateUtil.formatDateTime(order.getPaymentTime()));
        orderVo.setSendTime(DateUtil.formatDateTime(order.getSendTime()));
        orderVo.setEndTime(DateUtil.formatDateTime(order.getEndTime()));
        orderVo.setCreateTime(DateUtil.formatDateTime(order.getCreateTime()));
        orderVo.setCloseTime(DateUtil.formatDateTime(order.getCloseTime()));
        orderVo.setImageHost(ImageHost);


        List<OrderItemVo> orderItemVoList = CollUtil.newArrayList();

        for(OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    @Override
    public ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
        return shippingVo;
    }

    @Override
    public OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateUtil.formatDateTime(orderItem.getCreateTime()));
        return orderItemVo;
    }

    @Override
    public List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId){
        List<OrderVo> orderVoList = CollUtil.newArrayList();
        for(Order order : orderList){
            List<OrderItem>  orderItemList = CollUtil.newArrayList();
            if(userId == null){
                //todo 管理员查询的时候 不需要传userId
                orderItemList = orderItemDao.selectByOrderNo(order.getOrderNo());
            }else{
                orderItemList = orderItemDao.getByOrderNoUserId(order.getOrderNo(),userId);
            }
            OrderVo orderVo = assembleOrderVo(order,orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    @Override
    public ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(ImageHost);
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    @Override
    public ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(ImageHost);

        Category category = categoryDao.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateUtil.formatDateTime(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateUtil.formatDateTime(product.getUpdateTime()));
        return productDetailVo;
    }
}
