package cn.edu.xmu.order.factory;

import cn.edu.xmu.goodsprovider.activity.PreGroInner;
import cn.edu.xmu.order.model.po.OrderItemPo;

import cn.edu.xmu.order.model.vo.OrderVo;
import cn.edu.xmu.order.util.CreateOrderService;
import lombok.extern.slf4j.Slf4j;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public  class CreateOrderFactory {

    @Autowired
    private ApplicationContext applicationContext;


    @DubboReference(version = "0.0.1",check = false)
    private PreGroInner preGroInner;

    public CreateOrderService createService(OrderVo vo){

        List<OrderItemPo> itemPos=vo.createOrderItemsPo();
        ArrayList<Long> skuIds=new ArrayList<>(itemPos.size());
        for (OrderItemPo o:itemPos) {
            skuIds.add(o.getGoodsSkuId());
        }
        if (vo.getGrouponId()!=null){
            boolean effective=preGroInner.checkGrouponSkuId(vo.getPresaleId(),skuIds.get(0));
            if (effective){
                return applicationContext.getBean("GroOrderService", CreateOrderService.class);
            }
        }
        if (vo.getPresaleId()!=null){
            boolean effective=preGroInner.checkPresaleSkuId(vo.getPresaleId(),skuIds.get(0));
            if (effective){

                return applicationContext.getBean("PreOrderService", CreateOrderService.class);
            }
        }
        System.out.println("####");
        return applicationContext.getBean("NorOrderService", CreateOrderService.class);

    }
}
