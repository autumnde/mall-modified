package cn.zhang.mallmodified.dao;

import cn.zhang.mallmodified.po.Order;
import cn.zhang.mallmodified.po.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemDao {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    /**
     * 批量插入OrderItem
     * @param orderItemList
     */
    void batchInsert(@Param("orderItemList")List<OrderItem> orderItemList);

    int deleteByOrderNo(long orderNo);

    List<OrderItem> selectByOrderNo(long orderNo);

    List<OrderItem> getByOrderNoUserId(Long orderNo,Integer userId);
}