package cn.edu.xmu.order.util.factory;

import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.vo.NewOrderVo;
import cn.edu.xmu.order.service.GrouponOrderService;
import cn.edu.xmu.order.service.NormalOrderService;
import cn.edu.xmu.order.service.PresaleOrderService;
import cn.edu.xmu.order.service.impl.GoodsServiceImpl;
import cn.edu.xmu.order.service.impl.PostOrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
public  class PostOrderFactory {

    @Autowired
    private ApplicationContext applicationContext;

    public PostOrderServiceImpl createService(NewOrderVo vo){
        List<OrderItemPo> itemPos=vo.createOrderItemsPo();
        ArrayList<Long> skuIds=new ArrayList<>(itemPos.size());
        for (OrderItemPo o:itemPos) {
            skuIds.add(o.getGoodsSkuId());
        }
        //todo
        GoodsServiceImpl goodsService=null;

        if (vo.getGrouponId()!=null){
            boolean effective=goodsService.checkGrouponSkuId(vo.getPresaleId(),skuIds);
            if (effective){
                return applicationContext.getBean("GroOrderService",PostOrderServiceImpl.class);
            }
        }
        if (vo.getPresaleId()!=null){
            boolean effective=goodsService.checkPresaleSkuId(vo.getPresaleId(),skuIds);
            if (effective){
                return applicationContext.getBean("PreOrderService",PostOrderServiceImpl.class);
            }
        }
        return applicationContext.getBean("NorOrderService",PostOrderServiceImpl.class);
    }
}
