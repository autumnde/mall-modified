package cn.zhang.mallmodified.controller.portal;

import cn.zhang.mallmodified.common.api.ResponseCode;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.security.AdminUserDetails;
import cn.zhang.mallmodified.service.ICartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;
import java.security.Principal;

/**
 * @author autum
 */
@Api(tags = "购物车API")
@CrossOrigin
@RestController
@RequestMapping("/cart/")
@Validated
public class CartController {
    @Autowired
    private ICartService cartService;

    @ApiOperation("展示购物车列表")
    @RequestMapping(value = "list.do",method = RequestMethod.GET)
    public ServerResponse getCartList(Principal principal){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.list(user.getId()));
    }

    @ApiOperation("添加产品到购物车中")
    @RequestMapping(value = "add",method = RequestMethod.GET)
    public ServerResponse addCart(Principal principal,
                                  @ApiParam(value = "产品id",required = true)
                                  @NotBlank(message = "产品id不能为空") Integer productId,
                                  @ApiParam(value = "产品数量",required = true)
                                  @NotBlank(message = "产品数量不能为空") Integer count){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.addCart(user.getId(),productId,count));
    }

    @ApiOperation("更新购物车内某个产品的数量，即直接改为某个值")
    @RequestMapping(value = "update",method = RequestMethod.GET)
    public ServerResponse updateCart(Principal principal,
                                     @ApiParam(value = "产品id",required = true)
                                     @NotBlank(message = "产品id不能为空")Integer productId,
                                     @ApiParam(value = "产品数量",required = true)
                                         @NotBlank(message = "产品数量不能为空")Integer count){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.updateCart(user.getId(),productId,count));
    }

    @ApiOperation("删除购物车内多个产品")
    @RequestMapping(value = "delete_product",method = RequestMethod.GET)
    public ServerResponse deleteCart(Principal principal,
                                     @ApiParam(value = "产品id集合（请以‘,’为间隔,英文输入法）",required = true)
                                     @NotBlank(message = "请输入产品id集合")String productIds){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.deleteCart(user.getId(),productIds));
    }

    @ApiOperation("勾选购物车内某个产品")
    @RequestMapping(value = "select",method = RequestMethod.GET)
    public ServerResponse select(Principal principal,
                                 @ApiParam(value = "产品id",required = true)
                                 @NotBlank(message = "产品id不能为空")Integer productId){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.selectOrUnSelect(user.getId(),productId,1));
    }

    @ApiOperation("取消勾选购物车内某个产品")
    @RequestMapping(value = "un_select",method = RequestMethod.GET)
    public ServerResponse unselect(Principal principal,
                                   @ApiParam(value = "产品id",required = true)
                                   @NotBlank(message = "产品id不能为空")Integer productId){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.selectOrUnSelect(user.getId(),productId,0));
    }

    @ApiOperation("勾选购物车内全部产品")
    @RequestMapping(value = "select_all",method = RequestMethod.GET)
    public ServerResponse selectALL(Principal principal){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.selectOrUnselectAll(user.getId(),1));
    }

    @ApiOperation("取消购物车内全部产品的勾选")
    @RequestMapping(value = "un_select_all",method = RequestMethod.GET)
    public ServerResponse unSelectALL(Principal principal){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.selectOrUnselectAll(user.getId(),0));
    }

    @ApiOperation("获取购物车内产品总数量")
    @RequestMapping(value = "get_cart_product_count",method = RequestMethod.GET)
    public ServerResponse getCartProductNum(Principal principal){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.getCartProductNum(user.getId()));
    }
}
