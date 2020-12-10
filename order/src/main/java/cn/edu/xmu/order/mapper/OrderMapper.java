package cn.edu.xmu.order.mapper;

import cn.edu.xmu.order.model.po.OrderPo;

import java.util.List;

public interface OrderMapper {

    List<String> getAllOrderSn();

    List<Byte> getAllOrderStatesByCusId(Long customerId);

}
