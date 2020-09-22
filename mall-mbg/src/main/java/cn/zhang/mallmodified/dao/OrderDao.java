package cn.zhang.mallmodified.dao;

import cn.zhang.mallmodified.po.Order;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author autum
 */
@Repository
public interface OrderDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNo(Long orderNo);

    Order selectByUserIdAndOrderNo(Long orderNo,Integer userId);

    List<Order> selectByUserId(Integer userId);

    List<Order> selectAllOrder();
}