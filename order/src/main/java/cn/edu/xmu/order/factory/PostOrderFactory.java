package cn.edu.xmu.order.factory;

import cn.edu.xmu.order.model.po.OrderItemPo;

import cn.edu.xmu.order.model.vo.OrderVo;
import cn.edu.xmu.order.util.PostOrderService;
import cn.edu.xmu.produce.goods.IGoodsService;
import lombok.extern.slf4j.Slf4j;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public  class PostOrderFactory {

    @Autowired
    private ApplicationContext applicationContext;


    @DubboReference
    private IGoodsService goodsService;


    public PostOrderService createService(OrderVo vo){

        List<OrderItemPo> itemPos=vo.createOrderItemsPo();
        ArrayList<Long> skuIds=new ArrayList<>(itemPos.size());
        for (OrderItemPo o:itemPos) {
            skuIds.add(o.getGoodsSkuId());
        }
        if (vo.getGrouponId()!=null){
            boolean effective=goodsService.checkGrouponSkuId(vo.getPresaleId(),skuIds.get(0));
            if (effective){
                return applicationContext.getBean("GroOrderService", PostOrderService.class);
            }
        }
        if (vo.getPresaleId()!=null){
            boolean effective=goodsService.checkPresaleSkuId(vo.getPresaleId(),skuIds.get(0));
            if (effective){

                return applicationContext.getBean("PreOrderService", PostOrderService.class);
            }
        }
        return applicationContext.getBean("NorOrderService", PostOrderService.class);

    }
}
