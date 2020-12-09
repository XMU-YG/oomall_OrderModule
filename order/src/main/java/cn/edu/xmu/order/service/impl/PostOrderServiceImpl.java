package cn.edu.xmu.order.service.impl;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.vo.NewOrderVo;

public interface PostOrderServiceImpl {
    public ReturnObject<VoObject> addNewOrderByCustomer(Long customerId, NewOrderVo vo);
}
