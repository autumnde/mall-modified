package cn.zhang.mallmodified.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.dao.ShippingDao;
import cn.zhang.mallmodified.po.Shipping;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.service.IShippingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author autum
 */
@Service
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingDao shippingDao;
    @Override
    public ServerResponse addShipping(Shipping shipping) {
        shipping.setCreateTime(DateUtil.date());
        shipping.setUpdateTime(DateUtil.date());
        if(shippingDao.insert(shipping) == 0){
            return ServerResponse.createByErrorMessage("添加用户失败");
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse deleteShipping(Integer shippingId) {
        if(shippingDao.deleteByPrimaryKey(shippingId) == 0){
            return ServerResponse.createByErrorMessage("删除收货地址信息失败");
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse updateShipping(Shipping shipping) {
        if(shippingDao.updateByPrimaryKeySelective(shipping) == 0){
            return ServerResponse.createByErrorMessage("更新收货地址信息失败");
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse selectShipping(Integer shippingId) {
        Shipping shipping = shippingDao.selectByPrimaryKey(shippingId);
        if(shipping == null){
            return ServerResponse.createByError();
        }
        return ServerResponse.createBySuccess(shipping);
    }

    @Override
    public ServerResponse listShipping(Integer userId, Integer pageNum, Integer pageSize) {
        if(userId == null){
            return ServerResponse.createByErrorMessage("用户编号错误");
        }
        List<Shipping> shippingList = shippingDao.selectShippingListByUserId(userId);
        PageHelper.startPage(pageNum,pageSize);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
