package cn.zhang.mallmodified.service;

import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.dto.ShippingAddDto;
import cn.zhang.mallmodified.dto.ShippingUpdateDto;
import cn.zhang.mallmodified.po.Shipping;

/**
 * @author autum
 */
public interface IShippingService {
    /**
     * 添加收货地址信息
     * @param shipping
     * @return
     */
    public ServerResponse addShipping(ShippingAddDto shippingAddDto, Integer userId);

    /**
     * 删除收货地址信息
     * @param shippingId
     * @return
     */
    public ServerResponse deleteShipping(Integer shippingId);

    /**
     * 更新收货地址信息
     * @param shippingUpdateDto
     * @param userId
     * @return
     */
    public ServerResponse updateShipping(ShippingUpdateDto shippingUpdateDto, Integer userId);

    /**
     * 查看收货地址信息
     * @param shippingId
     * @return
     */
    public ServerResponse selectShipping(Integer shippingId);

    /**
     * 获取收货地址信息列表
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse listShipping(Integer userId,Integer pageNum,Integer pageSize);
}
