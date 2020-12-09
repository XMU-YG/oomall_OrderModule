package cn.edu.xmu.order.mapper;

import java.util.List;

public interface OrderMapper {

    List<String> getAllOrderSn();

    List<Byte> getAllOrderStatesByCusId(Long customerId);
}
