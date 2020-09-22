package cn.zhang.mallmodified.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhang.mallmodified.common.api.Const;
import cn.zhang.mallmodified.dao.CartDao;
import cn.zhang.mallmodified.dao.ProductDao;
import cn.zhang.mallmodified.po.Cart;
import cn.zhang.mallmodified.po.Product;
import cn.zhang.mallmodified.service.ICartService;
import cn.zhang.mallmodified.service.ICommonService;
import cn.zhang.mallmodified.vo.CartProductVo;
import cn.zhang.mallmodified.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    @Autowired
    private ICommonService commonService;

    @Override
    public CartVo list(Integer userId) {
        CartVo cartVo = commonService.assembleCartVo(userId);
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

}
