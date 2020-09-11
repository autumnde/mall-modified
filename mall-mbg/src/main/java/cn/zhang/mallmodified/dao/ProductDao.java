package cn.zhang.mallmodified.dao;

import cn.zhang.mallmodified.po.Product;

import java.util.List;

public interface ProductDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    /**
     * 获取全部产品信息
     * @return
     */
    List<Product> selectAllProducts();
}