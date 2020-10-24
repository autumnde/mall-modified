package cn.zhang.mallmodified.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhang.mallmodified.common.api.Const;
import cn.zhang.mallmodified.common.api.ResponseCode;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.dao.CategoryDao;
import cn.zhang.mallmodified.dao.ProductDao;
import cn.zhang.mallmodified.po.Category;
import cn.zhang.mallmodified.po.Product;
import cn.zhang.mallmodified.service.ICategoryService;
import cn.zhang.mallmodified.service.ICommonService;
import cn.zhang.mallmodified.service.IProductService;
import cn.zhang.mallmodified.vo.ProductDetailVo;
import cn.zhang.mallmodified.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private ICategoryService categoryService;
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
        ProductDetailVo productDetailVo = commonService.assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productDao.selectAllProducts();
        List<ProductDetailVo> productVoList = new ArrayList<>();
        for(Product product:productList){
            ProductDetailVo productVo = commonService.assembleProductDetailVo(product);
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

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        if(StrUtil.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getMessage());
        }
        List<Integer> categoryIdList = new ArrayList<Integer>();


        if(categoryId != null){
            Category category = categoryDao.selectByPrimaryKey(categoryId);
            if(category == null && StrUtil.isBlank(keyword) && categoryId != 0){
                //没有该分类,并且还没有关键字,这个时候返回一个空的结果集,不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            if(category != null || categoryId == 0){
                Integer termId = categoryId != 0?category.getId():0;
                categoryIdList = categoryService.selectCategoryAndChildrenById(termId).getData();
            }
        }

        if(StrUtil.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if(StrUtil.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }

        keyword = StrUtil.isBlank(keyword)?null:keyword;
        categoryIdList = categoryIdList.size()==0?null:categoryIdList;
        List<Product> productList = productDao.selectByNameAndCategoryIds(keyword,categoryIdList);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo productListVo = commonService.assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
