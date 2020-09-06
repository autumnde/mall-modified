package cn.zhang.mallmodified.controller.backend;

import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.model.User;
import cn.zhang.mallmodified.service.ICategoryService;
import cn.zhang.mallmodified.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author autum
 */
@RestController
@RequestMapping("/manager/category")
public class CategoryManagerController {
    @Autowired
    private IUserService userService;
    @Autowired
    private ICategoryService categoryService;

    @RequestMapping("addCategory.do")
    public ServerResponse addCategory(HttpSession httpSession, String categoryName, @RequestParam(defaultValue = "0")int parentId){
        User user = (User) httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        ServerResponse response1 = userService.checkAdminRole(user);
        if(!response1.isSuccess()){
            return ServerResponse.createByErrorMessage("无管理员权限");
        }
        return categoryService.addCategory(categoryName,parentId);
    }

    @RequestMapping("updateCategoryName.do")
    public ServerResponse updateCategoryName(HttpSession httpSession,Integer categoryId,String categoryName){
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        if(!userService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("无管理权限");
        }
        return categoryService.updateCategoryName(categoryId,categoryName);
    }

    @RequestMapping("getChildrenCategory.do")
    public ServerResponse getChildrenCategory(HttpSession httpSession,Integer parentId){
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        if(!userService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("无管理权限");
        }
        return categoryService.getChildCategoryOf(parentId);
    }

    @RequestMapping("getAllChildrenCategory.do")
    public ServerResponse getAllChildrenCategory(HttpSession httpSession,Integer parentId){
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        if(!userService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("无管理权限");
        }
        return categoryService.getAllChildCategoryOf(parentId);
    }
}
