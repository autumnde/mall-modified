package cn.zhang.mallmodified.controller.portal;


import cn.zhang.mallmodified.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author autum
 */
@RestController
public class ProductController {
    @Autowired
    private IProductService productService;
}
