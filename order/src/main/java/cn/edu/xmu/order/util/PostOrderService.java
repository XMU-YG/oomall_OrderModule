package cn.edu.xmu.order.util;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.vo.OrderVo;

public interface PostOrderService {
    public ReturnObject addNewOrderByCustomer(Long customerId, OrderVo vo);


}
