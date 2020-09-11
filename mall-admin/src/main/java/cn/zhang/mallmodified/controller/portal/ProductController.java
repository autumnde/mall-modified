package cn.zhang.mallmodified.controller.portal;


import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author autum
 */
@RestController
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService productService;

    @RequestMapping("detail.do")
    public ServerResponse getProductDetail(Integer productId){
        return productService.getProductDetail(productId);
    }

    @RequestMapping("list.do")
    public ServerResponse getProductList(){
        return null;
    }


}
