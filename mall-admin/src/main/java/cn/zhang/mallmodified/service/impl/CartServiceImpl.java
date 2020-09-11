package cn.zhang.mallmodified.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhang.mallmodified.common.api.Const;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.dao.CartDao;
import cn.zhang.mallmodified.dao.ProductDao;
import cn.zhang.mallmodified.po.Cart;
import cn.zhang.mallmodified.po.Product;
import cn.zhang.mallmodified.service.ICartService;
import cn.zhang.mallmodified.vo.CartVo;
import cn.zhang.mallmodified.vo.ProductInCartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author autum
 */
@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartDao cartDao;
    @Autowired
    private ProductDao productDao;
    @Value("${ftp.server.host}")
    private String imageUrl;

    @Override
    public CartVo list(Integer userId) {
        CartVo cartVo = new CartVo();
        cartVo.setUserId(userId);
        //获取某用户购物车内的全部产品（无论是否勾选）
        List<Cart> cartList = cartDao.selectByUserID(userId);
        List<ProductInCartVo> productInCartVoList = new ArrayList<>();
        for(Cart cart:cartList){
            Product product = productDao.selectByPrimaryKey(cart.getProductId());
            if(product != null){
                ProductInCartVo productInCartVo = new ProductInCartVo(product,cart.getChecked(),cart.getQuantity());
                productInCartVoList.add(productInCartVo);
            }
            //判断商品是否被勾选，以此确定是否要更改购物车的总价格
            if(cart.getChecked() == Const.Cart.CHECKED){
                cartVo.setTotalPrice(NumberUtil.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue())+cartVo.getTotalPrice());
            }
        }
        //设定购物车货品信息列表
        cartVo.setProductVoList(productInCartVoList);
        //获取购物车是否打算清空
        cartVo.setAllSelected(isAllChecked(userId));
        cartVo.setImageHost("");
        cartVo.setImageHost(imageUrl);
        return cartVo;
    }

    @Override
    public CartVo addCart(Integer userId, Integer productId, int count) {
        Cart cart = cartDao.selectByUserIdProductId(userId,productId);
        if(cart == null){
            //原先购物车不含有这个产品
            Cart cart1 = new Cart();
            cart1.setChecked(Const.Cart.CHECKED);
            cart1.setCreateTime(DateUtil.date());
            cart1.setProductId(productId);
            cart1.setQuantity(count);
            cart1.setUpdateTime(DateUtil.date());
            cart1.setUserId(userId);
            cartDao.insert(cart1);
        }
        else{
            //购物车原先就含有这个物品
            cart.setQuantity(cart.getQuantity()+count);
            cartDao.updateByPrimaryKeySelective(cart);
        }
        return list(userId);
    }

    @Override
    public CartVo updateCart(Integer userId, Integer productId, int count) {
        Cart cart = cartDao.selectByUserIdProductId(userId,productId);
        if(cart == null){
            //原先购物车不含有这个产品
            Cart cart1 = new Cart();
            cart1.setChecked(Const.Cart.CHECKED);
            cart1.setCreateTime(DateUtil.date());
            cart1.setProductId(productId);
            cart1.setQuantity(count);
            cart1.setUpdateTime(DateUtil.date());
            cart1.setUserId(userId);
        }
        else{
            //购物车原先就含有这个物品
            cart.setQuantity(count);
            cartDao.updateByPrimaryKeySelective(cart);
        }
        return list(userId);
    }

    @Override
    public CartVo deleteCart(Integer userId, String productIds) {
        List<String> productList = StrUtil.split(productIds,',');
        if(CollUtil.isNotEmpty(productList)){
            cartDao.deleteByUserIdProductIds(userId,productList);
            return list(userId);
        }
        return list(userId);
    }

    @Override
    public CartVo selectOrUnSelect(Integer userId, Integer productId,Integer checked) {
        cartDao.checkedOrUncheckedProduct(userId,productId,checked);
        return list(userId);
    }

    @Override
    public CartVo selectOrUnselectAll(Integer userId,Integer checked) {
        cartDao.checkedOrUncheckedProduct(userId,null,checked);
        return list(userId);
    }

    @Override
    public int getCartProductNum(Integer userId) {
        return cartDao.selectCartProductNumByUserId(userId);
    }


    /**
     * 判断某人的购物车是否全选
     * @param userId
     * @return
     */
    private boolean isAllChecked(Integer userId){
        if(userId == null){
            return false;
        }
        return cartDao.selectCheckedNumOf(userId) == 0;
    }
}
