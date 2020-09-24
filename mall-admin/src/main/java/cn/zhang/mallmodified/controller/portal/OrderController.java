package cn.zhang.mallmodified.controller.portal;

import cn.zhang.mallmodified.common.api.ResponseCode;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.security.AdminUserDetails;
import cn.zhang.mallmodified.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 * @author autum
 */
@Api(tags = "订单API")
@CrossOrigin
@RestController
@RequestMapping("/order/")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    @ApiOperation("根据收货地址创建订单")
    @RequestMapping(value = "create.do",method = RequestMethod.GET)
    public ServerResponse create(Principal principal, @ApiParam("收货地址id")Integer shippingId){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return orderService.createOrder(user.getId(),shippingId);
    }

    @ApiOperation("根据订单No取消订单")
    @RequestMapping(value = "cancel.do",method = RequestMethod.GET)
    public ServerResponse cancel(Principal principal,@ApiParam("订单No")Long orderNo){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        orderService.cancelOrder(orderNo);
        return ServerResponse.createBySuccess();
    }

    @ApiOperation("获取订单购物车内的产品")
    @RequestMapping(value = "get_order_cart_product.do", method = RequestMethod.GET)
    public ServerResponse getOrderCartProduct(Principal principal){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return orderService.getOrderCartProduct(user.getId());
    }

    @ApiOperation("获取订单列表")
    @RequestMapping(value = "list.do",method = RequestMethod.GET)
    public ServerResponse list(Principal principal,
                               @RequestParam(value = "pageNum",defaultValue = "1") @ApiParam("列表页数") int pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10") @ApiParam("每页展示的数量")int pageSize){
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        return orderService.getOrderList(user.getId(),pageNum,pageSize);
    }

    @ApiOperation("获取对应No的订单详细信息")
    @RequestMapping(value = "detail.do",method = RequestMethod.GET)
    public ServerResponse detail(Principal principal,@ApiParam("订单No")Long orderNo){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return orderService.getOrderDetail(user.getId(),orderNo);
    }
}
