package cn.zhang.mallmodified.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhang.mallmodified.common.api.Const;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.dao.CategoryDao;
import cn.zhang.mallmodified.dao.ProductDao;
import cn.zhang.mallmodified.po.Category;
import cn.zhang.mallmodified.po.Product;
import cn.zhang.mallmodified.service.ICommonService;
import cn.zhang.mallmodified.service.IProductService;
import cn.zhang.mallmodified.vo.ProductDetailVo;
import cn.zhang.mallmodified.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author autum
 */
@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ICommonService commonService;
    @Value(value = "${ftp.server.host}")
    private String FTP_SERVER_HOST;

    @Override
    public ServerResponse insertOrUpdateProduct(Product product) {
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品参数错误");
        }
        if (!StrUtil.isBlank(product.getSubImages())) {
            String[] subImages = product.getSubImages().split(",");
            if (subImages.length > 0) {
                product.setMainImage(subImages[0]);
            }
        }

        //更新产品信息
        if (product.getId() != null) {
            int rowCount = productDao.updateByPrimaryKey(product);
            if (rowCount > 0) {
                return ServerResponse.createBySuccess("更新产品成功");
            }
            return ServerResponse.createBySuccess("更新产品失败");
        } else {
            int rowCount = productDao.insert(product);
            if (rowCount > 0) {
                return ServerResponse.createBySuccess("新增产品成功");
            }
            return ServerResponse.createBySuccess("新增产品失败");
        }
    }

    @Override
    public ServerResponse setProductStae(Integer productId, Integer state) {
        if(productId == null || state == null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Product product = new Product();
        product.setStatus(state);
        product.setId(productId);
        int influence = productDao.updateByPrimaryKeySelective(product);
        if(influence == 0){
            return ServerResponse.createByErrorMessage("产品状态修改失败");
        }
        return ServerResponse.createBySuccessMessage("产品状态修改成功");
    }

    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if(productId == null){
            return ServerResponse.createByErrorMessage("参数错误");
        }

        Product product = productDao.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品不存在");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productDao.selectAllProducts();
        List<ProductDetailVo> productVoList = new ArrayList<>();
        for(Product product:productList){
            ProductDetailVo productVo = assembleProductDetailVo(product);
            productVoList.add(productVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if(StrUtil.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productDao.selectByNameAndProductId(productName,productId);
        List<ProductListVo> productListVoList = CollUtil.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo = commonService.assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setImageHost(FTP_SERVER_HOST);

        Category category = categoryDao.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            //默认根节点
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateUtil.formatDateTime(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateUtil.formatDateTime(product.getUpdateTime()));
        return productDetailVo;
    }
}
