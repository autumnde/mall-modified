package cn.zhang.mallmodified.service;

import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.po.Product;

/**
 * @author autum
 */
public interface IProductService {
    /**
     * 新增或更新产品
     * @param product
     * @return
     */
    public ServerResponse insertOrUpdateProduct(Product product);

    /**
     * 修改产品上架状态
     * @param productId
     * @param state
     * @return
     */
    public ServerResponse setProductStae(Integer productId,Integer state);

    /**
     * 获取产品详细信息
     * @param productId
     * @return
     */
    public ServerResponse getProductDetail(Integer productId);

    /**
     * 获取产品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse getProductList(int pageNum,int pageSize);
}
