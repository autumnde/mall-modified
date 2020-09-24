package cn.zhang.mallmodified.dao;

import cn.zhang.mallmodified.po.Product;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectAllProducts();

    List<Product> selectByNameAndProductId(String productName,Integer productId);

    List<Product> selectByNameAndCategoryIds(@Param("productName")String productName, @Param("categoryIdList")List<Integer> categoryIdList);
}