package cn.zhang.mallmodified.service;

import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.vo.OrderVo;
import com.github.pagehelper.PageInfo;

/**
 * 主要负责数据转化的活
 * @author autum
 */
public interface IOrderService {
    /**
     * 根据用户名和收货地址生成订单
     * @param userId
     * @param shippingId
     * @return
     */
    public ServerResponse createOrder(Integer userId,Integer shippingId);

    /**
     * 取消订单
     * @param orderId
     * @return
     */
    public ServerResponse cancelOrder(long orderId);

    /**
     * 获取用户订单信息
     * @return
     */
    public ServerResponse getOrderDetail(Integer userId,Long orderNo);

    /**
     * 获取用户订单购物车产品
     * @param userId
     * @return
     */
    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    ServerResponse<PageInfo> manageList(int pageNum,int pageSize);

    public long generateOrderNo();

    ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);

    ServerResponse<String> manageSendGoods(Long orderNo);
}
