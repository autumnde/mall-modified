package cn.zhang.mallmodified.controller.portal;

import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author autum
 */
@RestController
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService cartService;

    @RequestMapping("list.do")
    public ServerResponse getCartList(HttpSession httpSession){
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return ServerResponse.createBySuccess(cartService.list(user.getId()));
    }

    @RequestMapping("add.do")
    public ServerResponse addCart(HttpSession httpSession,Integer productId,Integer count){
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return ServerResponse.createBySuccess(cartService.addCart(user.getId(),productId,count));
    }

    @RequestMapping("update.do")
    public ServerResponse updateCart(HttpSession httpSession,Integer productId,Integer count){
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return ServerResponse.createBySuccess(cartService.updateCart(user.getId(),productId,count));
    }

    @RequestMapping("del.do")
    public ServerResponse deleteCart(HttpSession httpSession,String productIds){
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return ServerResponse.createBySuccess(cartService.deleteCart(user.getId(),productIds));
    }

    @RequestMapping("select.do")
    public ServerResponse select(HttpSession httpSession,Integer productId){
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return ServerResponse.createBySuccess(cartService.selectOrUnSelect(user.getId(),productId,1));
    }

    @RequestMapping("unselect.do")
    public ServerResponse unselect(HttpSession httpSession,Integer productId){
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return ServerResponse.createBySuccess(cartService.selectOrUnSelect(user.getId(),productId,0));
    }

    @RequestMapping("selectAll.do")
    public ServerResponse selectALL(HttpSession httpSession){
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return ServerResponse.createBySuccess(cartService.selectOrUnselectAll(user.getId(),1));
    }

    @RequestMapping("unselectAll.do")
    public ServerResponse unSelectALL(HttpSession httpSession){
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return ServerResponse.createBySuccess(cartService.selectOrUnselectAll(user.getId(),0));
    }

    @RequestMapping("cartProductNum.do")
    public ServerResponse getCartProductNum(HttpSession httpSession){
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return ServerResponse.createBySuccess(cartService.getCartProductNum(user.getId()));
    }
}
