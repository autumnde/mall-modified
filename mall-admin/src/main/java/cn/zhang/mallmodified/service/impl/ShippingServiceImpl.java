package cn.zhang.mallmodified.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.dao.ShippingDao;
import cn.zhang.mallmodified.dto.ShippingAddDto;
import cn.zhang.mallmodified.dto.ShippingUpdateDto;
import cn.zhang.mallmodified.po.Shipping;
import cn.zhang.mallmodified.service.ICommonService;
import cn.zhang.mallmodified.service.IShippingService;
import cn.zhang.mallmodified.vo.ShippingVo;
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
    @Autowired
    private ICommonService commonService;
    @Override
    public ServerResponse addShipping(ShippingAddDto shippingAddDto,Integer userId){
        Shipping shipping = commonService.assembleShipping(shippingAddDto,userId);
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
    public ServerResponse updateShipping(ShippingUpdateDto shippingUpdateDto, Integer userId) {
        //将收货地址更新信息转化为shipping
        Shipping shipping = commonService.assembleShipping(shippingUpdateDto,userId);
        //对数据库进行修改
        if(shippingDao.updateByPrimaryKeySelective(shipping) == 0){
            return ServerResponse.createByErrorMessage("更新收货地址信息失败");
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse selectShipping(Integer shippingId) {
        Shipping shipping = shippingDao.selectByPrimaryKey(shippingId);
        return ServerResponse.createBySuccess(shipping);
    }

    @Override
    public ServerResponse listShipping(Integer userId, Integer pageNum, Integer pageSize) {
        List<Shipping> shippingList = shippingDao.selectShippingListByUserId(userId);
        PageHelper.startPage(pageNum,pageSize);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
