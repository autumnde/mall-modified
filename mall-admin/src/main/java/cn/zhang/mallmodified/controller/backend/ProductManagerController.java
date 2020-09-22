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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 * @author autum
 */
@RestController
@RequestMapping("/manager/product")
public class ProductManagerController {
    @Autowired
    private IProductService productService;
    @Autowired
    private ICommonService commonService;
    @Autowired
    private IUserService userService;
    @Autowired
    FtpUtils ftpUtils;

    @RequestMapping("save.do")
    public ServerResponse productSave(HttpSession httpSession, Product product){
        ServerResponse tempResponse = commonService.AdminJudge(httpSession);
        if(!tempResponse.isSuccess()){
            return tempResponse;
        }
        return productService.insertOrUpdateProduct(product);

    }

    @RequestMapping("set_sale_status.do")
    public ServerResponse setProductState(HttpSession httpSession,Integer productId,Integer status){
        ServerResponse tempResponse = commonService.AdminJudge(httpSession);
        if(!tempResponse.isSuccess()){
            return tempResponse;
        }
        return productService.setProductStae(productId,status);
    }

    @RequestMapping("detail.do")
    public ServerResponse getProductDetail(HttpSession httpSession,Integer productId){
        ServerResponse tempResponse = commonService.AdminJudge(httpSession);
        if(!tempResponse.isSuccess()){
            return tempResponse;
        }
        return productService.getProductDetail(productId);
    }

    @RequestMapping("list.do")
    public ServerResponse getProductList(HttpSession httpSession, @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,@RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        ServerResponse tempResponse = commonService.AdminJudge(httpSession);
        if(!tempResponse.isSuccess()){
            return tempResponse;
        }
        return productService.getProductList(pageNum,pageSize);
    }

    @PostMapping("upload.do")
    public ServerResponse upload(HttpSession httpSession,@RequestParam(value = "upLoadFile",required = false) MultipartFile file){
        ServerResponse tempResponse = commonService.AdminJudge(httpSession);
        if(!tempResponse.isSuccess()){
            return tempResponse;
        }
        //上传文件为空
        if(file == null){
            return ServerResponse.createByErrorMessage("上传文件内容为空");
        }
        return ftpUtils.upload(file);
    }

    @RequestMapping("search.do")
    public ServerResponse productSearch(Principal principal, String productName, Integer productId,
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
