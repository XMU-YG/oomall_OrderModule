package cn.edu.xmu.order.factory;

import cn.edu.xmu.order.factory.impl.IdExistImpl;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.vo.NewOrderVo;
import cn.edu.xmu.order.service.GrouponOrderService;
import cn.edu.xmu.order.service.NormalOrderService;
import cn.edu.xmu.order.service.PresaleOrderService;
import cn.edu.xmu.order.service.impl.GoodsServiceImpl;
import cn.edu.xmu.order.service.impl.PostOrderServiceImpl;
import org.mapstruct.ObjectFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public  class PostOrderFactory {
    public static Class createService(NewOrderVo vo){
        List<OrderItemPo> itemPos=vo.createOrderItemsPo();
        ArrayList<Long> skuIds=new ArrayList<>(itemPos.size());
        for (OrderItemPo o:itemPos) {
            skuIds.add(o.getGoodsSkuId());
        }
        GoodsServiceImpl goodsService=null;

        if (vo.getGrouponId()!=null){
            boolean effective=goodsService.checkGrouponSkuId(vo.getPresaleId(),skuIds);
            if (effective){
                return GrouponOrderService.class;
            }
        }
        if (vo.getPresaleId()!=null){
            boolean effective=goodsService.checkPresaleSkuId(vo.getPresaleId(),skuIds);
            if (effective){
                return PresaleOrderService.class;
            }
        }
        return NormalOrderService.class;
    }
}
