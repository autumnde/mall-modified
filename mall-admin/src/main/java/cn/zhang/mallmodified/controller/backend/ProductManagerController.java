package cn.zhang.mallmodified.controller.backend;

import cn.zhang.mallmodified.common.api.ResponseCode;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.common.utils.FtpUtils;
import cn.zhang.mallmodified.po.Product;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.security.AdminUserDetails;
import cn.zhang.mallmodified.service.ICommonService;
import cn.zhang.mallmodified.service.IProductService;
import cn.zhang.mallmodified.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.security.Principal;

/**
 * @author autum
 */
@Api(tags = "商品管理API")
@RestController
@RequestMapping("/manager/product")
@Validated
public class ProductManagerController {
    @Autowired
    private IProductService productService;
    @Autowired
    private IUserService userService;
    @Autowired
    FtpUtils ftpUtils;

    @ApiOperation("上传某个商品信息(如果该产品已经存在则更新信息，否则添加新产品)")
    @RequestMapping("save")
    public ServerResponse productSave(Principal principal,
                                      @ApiParam(value = "添加产品的详细信息",required = true)
                                      @Valid Product product){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        if(userService.checkAdminRole(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return productService.insertOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }

    }

    @ApiOperation("设置产品状态")
    @RequestMapping("set_sale_status")
    public ServerResponse setProductState(Principal principal,
                                          @ApiParam(value = "产品id",required = true)
                                          @NotBlank(message = "产品id不能为空") Integer productId,
                                          @ApiParam(value = "产品状态",required = true)
                                          @NotBlank(message = "产品状态不能为空")Integer status){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        if(userService.checkAdminRole(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return productService.setProductStae(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }

    }

    @ApiOperation("根据id获取某个产品详细信息")
    @RequestMapping("detail")
    public ServerResponse getProductDetail(Principal principal,
                                           @ApiParam(value = "产品id",required = true)
                                           @NotBlank(message = "产品id不能为空")Integer productId){
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
        return productService.getProductDetail(productId);
    }

    @ApiOperation("展示所有的产品信息")
    @RequestMapping("list")
    public ServerResponse getProductList(Principal principal,
                                         @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
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
        return productService.getProductList(pageNum,pageSize);
    }

    @ApiOperation("上传图片到服务器中")
    @PostMapping("upload")
    public ServerResponse upload(Principal principal,
                                 @RequestParam(value = "upLoadFile",required = false)
                                 @ApiParam(value = "上传的文件")
                                 @NotBlank(message = "上传文件不能为空") MultipartFile file){
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
        return ftpUtils.upload(file);
    }

    @ApiOperation("根据关键字和id查询产品")
    @RequestMapping("search")
    public ServerResponse productSearch(Principal principal,
                                        String productName,
                                        Integer productId,
                                        @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        if(principal ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getMessage());
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        if(userService.checkAdminRole(user).isSuccess()){
            //填充业务
            return productService.searchProduct(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
}
