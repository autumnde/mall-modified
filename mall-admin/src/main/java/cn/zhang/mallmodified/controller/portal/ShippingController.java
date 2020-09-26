package cn.zhang.mallmodified.controller.portal;

import cn.zhang.mallmodified.common.api.ResponseCode;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.dto.ShippingAddDto;
import cn.zhang.mallmodified.dto.ShippingUpdateDto;
import cn.zhang.mallmodified.po.Shipping;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.security.AdminUserDetails;
import cn.zhang.mallmodified.service.IShippingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.security.Principal;

/**
 * @author autum
 */
@Api(tags = "收货地址API")
@CrossOrigin
@RestController
@RequestMapping("/shipping/")
@Validated
public class ShippingController {
    @Autowired
    private IShippingService shippingService;

    @ApiOperation("添加某个收货地址")
    @RequestMapping("add")
    public ServerResponse addShipping(Principal principal,
                                      @ApiParam("添加收货地址所需信息") ShippingAddDto shippingAddDto){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return shippingService.addShipping(shippingAddDto,user.getId());
    }

    @ApiOperation("删除某个收货地址")
    @RequestMapping("del")
    public ServerResponse deleteShipping(Principal principal,
                                         @ApiParam("收货地址id")@NotBlank(message = "收货地址id不能为空") Integer shippingId){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return shippingService.deleteShipping(shippingId);
    }

    @ApiOperation("更新某个收货地址")
    @RequestMapping("update")
    public ServerResponse updateShipping(Principal principal,
                                         @ApiParam("收货地址要更新的信息")@Valid ShippingUpdateDto shippingUpdateDto){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return shippingService.updateShipping(shippingUpdateDto,user.getId());
    }

    @ApiOperation("查看某个收货地址")
    @RequestMapping("select")
    public ServerResponse selectShipping(Principal principal,
                                         @ApiParam("收货地址id")@NotBlank(message = "收货地址id不能为空")Integer shippingId){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return shippingService.selectShipping(shippingId);
    }

    @ApiOperation("列出某人全部的收货地址")
    @RequestMapping("list")
    public ServerResponse listShipping(Principal principal,
                                       @RequestParam(defaultValue = "1")Integer pageNum,
                                       @RequestParam(defaultValue = "10")Integer pageSize){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return shippingService.listShipping(user.getId(),pageNum,pageSize);
    }
}
