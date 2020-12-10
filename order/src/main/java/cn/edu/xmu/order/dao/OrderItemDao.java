package cn.edu.xmu.order.dao;

import cn.edu.xmu.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.order.model.po.OrderItemPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemDao {

    @Autowired
    private OrderItemPoMapper orderItemPoMapper;

    public OrderItemPo getOrderItemById(Long id) {
        return orderItemPoMapper.selectByPrimaryKey(id);
    }

    public int updateOrderItem(OrderItemPo orderItemPo) {
        return orderItemPoMapper.updateByPrimaryKey(orderItemPo);
    }
}
