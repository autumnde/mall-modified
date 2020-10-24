package cn.zhang.mallmodified.service;

import cn.zhang.mallmodified.vo.CartVo;

/**
 * @author autum
 */
public interface ICartService {
    /**
     * 获取某用户的购物车详情
     * @param userId
     * @return
     */
    public CartVo list(Integer userId);

    /**
     * 向购物车中添加商品
     * @return
     */
    public CartVo addCart(Integer userId,Integer productId,int count);

    /**
     * 更新购物车商品信息
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public CartVo updateCart(Integer userId,Integer productId,int count);

    /**
     * 删除购物车内多个商品
     * @param userId
     * @param productIds
     * @return
     */
    public CartVo deleteCart(Integer userId,String productIds);

    /**
     * 修改某一商品是否被修改的状态
     * @param userId
     * @param productId
     * @param checked
     * @return
     */
    public CartVo selectOrUnSelect(Integer userId, Integer productId,Integer checked);

    /**
     * 修改某人的购物车是否勾选全选
     * @param userId
     * @return
     */
    public CartVo selectOrUnselectAll(Integer userId,Integer checked);

    /**
     * 获取购物车内所有商品合计的数量
     * @param userId
     * @return
     */
    public int getCartProductNum(Integer userId);
}
