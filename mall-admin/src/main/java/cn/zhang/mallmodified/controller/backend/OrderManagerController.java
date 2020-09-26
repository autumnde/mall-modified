package cn.zhang.mallmodified.controller.backend;

import cn.zhang.mallmodified.common.api.ResponseCode;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.security.AdminUserDetails;
import cn.zhang.mallmodified.service.IOrderService;
import cn.zhang.mallmodified.service.IUserService;
import cn.zhang.mallmodified.vo.OrderVo;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.security.Principal;

/**
 * @author autum
 */
@Api(tags = "订单管理API")
@RestController
@RequestMapping("/manager/order")
@Validated
public class OrderManagerController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IOrderService orderService;

    @ApiOperation("展示所有订单信息")
    @RequestMapping("list")
    public ServerResponse<PageInfo> orderList(Principal principal,
                                              @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        if(userService.checkAdminRole(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return orderService.manageList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @ApiOperation("展示某个订单的详细信息")
    @RequestMapping("detail")
    public ServerResponse<OrderVo> orderDetail(Principal principal,
                                               @ApiParam(value = "订单编号",required = true)
                                               @NotBlank(message = "订单编号不能为空") Long orderNo){

        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        if(userService.checkAdminRole(user).isSuccess()){
            return userService.manageDetail(orderNo);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }



    @ApiOperation("查询某个订单（模糊查询，只要部分重合均能查到）")
    @RequestMapping("search")
    public ServerResponse<PageInfo> orderSearch(Principal principal,
                                                @ApiParam(value = "订单编号",required = true)
                                                @NotBlank(message = "订单编号不能为空") Long orderNo,
                                                @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        if(userService.checkAdminRole(user).isSuccess()){
            return orderService.manageSearch(orderNo,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @ApiOperation("为某个订单发货")
    @RequestMapping("send_goods")
    public ServerResponse<String> orderSendGoods(Principal principal,
                                                 @ApiParam(value = "订单编号",required = true)
                                                 @NotBlank(message = "订单编号不能为空")Long orderNo){

        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        if(userService.checkAdminRole(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return orderService.manageSendGoods(orderNo);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
}
