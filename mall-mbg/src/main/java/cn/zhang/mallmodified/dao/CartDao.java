package cn.zhang.mallmodified.dao;

import cn.zhang.mallmodified.po.Cart;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List<Cart> selectByUserID(Integer userID);

    int selectCheckedNumOf(Integer userId);

    /**
     * 根据用户编号和产品编号获取购物车对应信息
     * @param userId
     * @param productId
     * @return
     */
    Cart selectByUserIdProductId(Integer userId,Integer productId);

    /**
     * 根据用户编号和产品编号删除购物车对应信息
     * @param userId
     * @param productIdList
     * @return
     */
    int deleteByUserIdProductIds(Integer userId,List<String> productIdList);

    int checkedOrUncheckedProduct(Integer userId,Integer productId,Integer checked);

    int selectCartProductNumByUserId(Integer userId);

    /**
     * 获取某用户已勾选的所有商品
     * @param userId
     * @return
     */
    List<Cart> selectCheckedCartByUserId(Integer userId);

}