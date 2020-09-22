package cn.zhang.mallmodified.controller.portal;

import cn.zhang.mallmodified.common.api.ResponseCode;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.security.AdminUserDetails;
import cn.zhang.mallmodified.service.ICartService;
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
@CrossOrigin
@RestController
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService cartService;

    @RequestMapping("list.do")
    public ServerResponse getCartList(Principal principal){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.list(user.getId()));
    }

    @RequestMapping("add.do")
    public ServerResponse addCart(Principal principal,Integer productId,Integer count){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.addCart(user.getId(),productId,count));
    }

    @RequestMapping("update.do")
    public ServerResponse updateCart(Principal principal,Integer productId,Integer count){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.updateCart(user.getId(),productId,count));
    }

    @RequestMapping("delete_product.do")
    public ServerResponse deleteCart(Principal principal,String productIds){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.deleteCart(user.getId(),productIds));
    }

    @RequestMapping("select.do")
    public ServerResponse select(Principal principal,Integer productId){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.selectOrUnSelect(user.getId(),productId,1));
    }

    @RequestMapping("un_select.do")
    public ServerResponse unselect(Principal principal,Integer productId){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.selectOrUnSelect(user.getId(),productId,0));
    }

    @RequestMapping("select_all.do")
    public ServerResponse selectALL(Principal principal){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.selectOrUnselectAll(user.getId(),1));
    }

    @RequestMapping("un_select_all.do")
    public ServerResponse unSelectALL(Principal principal){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return ServerResponse.createBySuccess(cartService.selectOrUnselectAll(user.getId(),0));
    }

    @RequestMapping("get_cart_product_count.do")
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
