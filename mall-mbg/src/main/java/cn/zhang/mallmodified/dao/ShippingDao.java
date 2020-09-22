package cn.zhang.mallmodified.dao;

import cn.zhang.mallmodified.po.Shipping;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShippingDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    List<Shipping> selectShippingListByUserId(Integer userId);
}