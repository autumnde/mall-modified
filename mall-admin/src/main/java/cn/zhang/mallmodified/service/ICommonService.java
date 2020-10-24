package cn.zhang.mallmodified.service;

import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.dto.ShippingAddDto;
import cn.zhang.mallmodified.dto.ShippingUpdateDto;
import cn.zhang.mallmodified.dto.UserRegisterDto;
import cn.zhang.mallmodified.po.*;
import cn.zhang.mallmodified.vo.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

public interface ICommonService {
    /**
     * 判断是否是管理员（未登录直接驳回）
     * @param httpSession
     * @return
     */
    public ServerResponse AdminJudge(HttpSession httpSession);

    /**
     * 生成某个用户的订单详细列表
     * @param userId
     * @return
     */
    public ServerResponse assembleOrderItemList(Integer userId);

    /**
     * 判断某个用户的购物车是否全选
     * @param userId
     * @return
     */
    public boolean isAllChecked(Integer userId);

    /**
     * 根据用户ID生成其对应的CartVo
     * @param userId
     * @return
     */
    public CartVo assembleCartVo(Integer userId);

    public Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment);

    public OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList);

    public ShippingVo assembleShippingVo(Shipping shipping);

    public OrderItemVo assembleOrderItemVo(OrderItem orderItem);

    public List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId);

    public ProductListVo assembleProductListVo(Product product);

    public ProductDetailVo assembleProductDetailVo(Product product);

    /**
     * UserRegisterDto转化为User
     * @param userRegisterDto
     * @return
     */
    public User assembleUser(UserRegisterDto userRegisterDto);

    public Shipping assembleShipping(ShippingUpdateDto shippingUpdateDto, Integer userId);

    public Shipping assembleShipping(ShippingAddDto shippingAddDto, Integer userId);
 }
