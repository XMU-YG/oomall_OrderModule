package cn.edu.xmu.order.util;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.vo.OrderVo;

/**
 * 创建订单服务
 */
public interface PostOrderService {
    public ReturnObject addNewOrderByCustomer(Long customerId, OrderVo vo);
    public boolean deductStock(Long actId,Long skuId,Integer quantity);
}
