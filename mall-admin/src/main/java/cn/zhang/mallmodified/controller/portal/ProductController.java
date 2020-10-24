package cn.zhang.mallmodified.controller.portal;


import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.service.IProductService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author autum
 */
@Api(tags = "产品API")
@CrossOrigin
@RestController
@RequestMapping("/product/")
@Validated
@Slf4j
public class ProductController {
    @Autowired
    private IProductService productService;

    @ApiOperation("获取某个产品的详细信息")
    @RequestMapping(value = "detail")
    public ServerResponse getProductDetail(@ApiParam(value = "产品id",required = true) @NotNull Integer productId){
        return productService.getProductDetail(productId);
    }

    @ApiOperation("根据产品的关键字和分类的id获取产品列表(0为所有id分类的父节点)")
    @RequestMapping(value = "list")
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false) @ApiParam("搜索关键字") String keyword,
                                         @RequestParam(value = "categoryId",required = false) @ApiParam("分类id")Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1") @ApiParam("页数")int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") @ApiParam("每页排放的元素数量")int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "") @ApiParam("排序参数：例如price_desc，price_asc")String orderBy){
        return productService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }


}
