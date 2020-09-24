package cn.zhang.mallmodified.controller.portal;

import cn.zhang.mallmodified.common.api.ResponseCode;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.po.Shipping;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.security.AdminUserDetails;
import cn.zhang.mallmodified.service.IShippingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 * @author autum
 */
@Api(tags = "收货地址API")
@CrossOrigin
@RestController
@RequestMapping("/shipping/")
public class ShippingController {
    @Autowired
    private IShippingService shippingService;

    @ApiOperation("添加某个收货地址")
    @RequestMapping("add.do")
    public ServerResponse addShipping(Principal principal, @ApiParam("收货地址详细信息") Shipping shipping){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        //确定用户id，但是名字不一定对应的上
        shipping.setUserId(user.getId());
        return shippingService.addShipping(shipping);
    }

    @ApiOperation("删除某个收货地址")
    @RequestMapping("del.do")
    public ServerResponse deleteShipping(Principal principal,@ApiParam("收货地址id") Integer shippingId){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return shippingService.deleteShipping(shippingId);
    }

    @ApiOperation("更新某个收货地址")
    @RequestMapping("update.do")
    public ServerResponse updateShipping(Principal principal,@ApiParam("收货地址要更新的信息") Shipping shipping){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        shipping.setUserId(user.getId());
        return shippingService.updateShipping(shipping);
    }

    @ApiOperation("查看某个收货地址")
    @RequestMapping("select.do")
    public ServerResponse selectShipping(Principal principal,@ApiParam("收货地址id") Integer shippingId){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return shippingService.selectShipping(shippingId);
    }

    @ApiOperation("列出某人全部的收货地址")
    @RequestMapping("list.do")
    public ServerResponse listShipping(Principal principal,Integer pageNum,Integer pageSize){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return shippingService.listShipping(user.getId(),pageNum,pageSize);
    }
}
