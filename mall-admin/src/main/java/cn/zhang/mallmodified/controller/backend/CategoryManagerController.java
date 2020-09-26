package cn.zhang.mallmodified.controller.backend;

import cn.zhang.mallmodified.common.api.ResponseCode;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.security.AdminUserDetails;
import cn.zhang.mallmodified.service.ICategoryService;
import cn.zhang.mallmodified.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;
import java.security.Principal;

/**
 * @author autum
 */
@RestController
@Api(tags = "分类管理API")
@RequestMapping("/manager/category")
@Validated
public class CategoryManagerController {
    @Autowired
    private IUserService userService;
    @Autowired
    private ICategoryService categoryService;

    @ApiOperation("添加分类")
    @RequestMapping("add_category")
    public ServerResponse addCategory(Principal principal,
                                      @ApiParam(value = "分类姓名",required = true)
                                      @NotBlank(message = "分类姓名不能为空")String categoryName,
                                      @RequestParam(defaultValue = "0")@ApiParam("该分类父节点编号（默认为0）")int parentId){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        ServerResponse response1 = userService.checkAdminRole(user);
        if(!response1.isSuccess()){
            return ServerResponse.createByErrorMessage("无管理员权限");
        }
        return categoryService.addCategory(categoryName,parentId);
    }

    @ApiOperation("设置分类姓名")
    @RequestMapping("set_category_name")
    public ServerResponse updateCategoryName(Principal principal,
                                             @ApiParam(value = "分类id",required = true)
                                             @NotBlank(message = "分类id不能为空") Integer categoryId,
                                             @ApiParam(value = "分类姓名",required = true)
                                             @NotBlank(message = "分类姓名不能为空") String categoryName){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        if(!userService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("无管理权限");
        }
        return categoryService.updateCategoryName(categoryId,categoryName);
    }

    @ApiOperation("获取分类信息")
    @RequestMapping("get_category")
    public ServerResponse getChildrenCategory(Principal principal,
                                              @ApiParam(value = "父节点id",required = true)
                                              @NotBlank(message = "父节点id不能为空") Integer parentId){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        if(!userService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("无管理权限");
        }
        return categoryService.getChildCategoryOf(parentId);
    }

    @ApiOperation("获取分类及其所有子分类信息")
    @RequestMapping("get_deep_category")
    public ServerResponse getAllChildrenCategory(Principal principal,
                                                 @ApiParam(value = "父节点id",required = true)
                                                 @NotBlank(message = "父节点id不能为空")Integer parentId){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        if(!userService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("无管理权限");
        }
        return categoryService.getAllChildCategoryOf(parentId);
    }
}
