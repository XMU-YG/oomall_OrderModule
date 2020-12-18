package cn.edu.xmu.order.util;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.vo.OrderVo;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.lang.reflect.InvocationTargetException;

/**
 * 创建订单服务
 */
public interface CreateOrderService {
    public ReturnObject createOrderByCustomer(Long customerId, OrderVo vo) throws JsonProcessingException, InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException;
    public boolean deductStock(Long actId,Long skuId,Integer quantity);
}
