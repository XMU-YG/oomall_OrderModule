package cn.edu.xmu.order.util.factory;

import cn.edu.xmu.order.model.po.OrderItemPo;
<<<<<<< Updated upstream:order/src/main/java/cn/edu/xmu/order/factory/PostOrderFactory.java
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
=======
import cn.edu.xmu.order.model.vo.OrderVo;
import cn.edu.xmu.order.util.PostOrderService;
import cn.edu.xmu.produce.IGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
>>>>>>> Stashed changes:order/src/main/java/cn/edu/xmu/order/util/factory/PostOrderFactory.java

import java.util.ArrayList;
import java.util.List;

<<<<<<< Updated upstream:order/src/main/java/cn/edu/xmu/order/util/factory/PostOrderFactory.java
@Component
public  class PostOrderFactory {
=======
<<<<<<< Updated upstream:order/src/main/java/cn/edu/xmu/order/factory/PostOrderFactory.java
@Service
public  class PostOrderFactory {
    public static Class createService(NewOrderVo vo){
=======
@Component
@Slf4j
public  class PostOrderFactory {
>>>>>>> Stashed changes:order/src/main/java/cn/edu/xmu/order/factory/PostOrderFactory.java

    @Autowired
    private ApplicationContext applicationContext;

<<<<<<< Updated upstream:order/src/main/java/cn/edu/xmu/order/util/factory/PostOrderFactory.java
    public PostOrderServiceImpl createService(NewOrderVo vo){
=======
    @DubboReference
    private IGoodsService goodsService;


    public PostOrderService createService(OrderVo vo){
>>>>>>> Stashed changes:order/src/main/java/cn/edu/xmu/order/util/factory/PostOrderFactory.java
>>>>>>> Stashed changes:order/src/main/java/cn/edu/xmu/order/factory/PostOrderFactory.java
        List<OrderItemPo> itemPos=vo.createOrderItemsPo();
        ArrayList<Long> skuIds=new ArrayList<>(itemPos.size());
        for (OrderItemPo o:itemPos) {
            skuIds.add(o.getGoodsSkuId());
        }
<<<<<<< Updated upstream:order/src/main/java/cn/edu/xmu/order/util/factory/PostOrderFactory.java
        //todo
=======
<<<<<<< Updated upstream:order/src/main/java/cn/edu/xmu/order/factory/PostOrderFactory.java
>>>>>>> Stashed changes:order/src/main/java/cn/edu/xmu/order/factory/PostOrderFactory.java
        GoodsServiceImpl goodsService=null;
=======
        //todo
>>>>>>> Stashed changes:order/src/main/java/cn/edu/xmu/order/util/factory/PostOrderFactory.java

        if (vo.getGrouponId()!=null){
            boolean effective=goodsService.checkGrouponSkuId(vo.getPresaleId(),skuIds.get(0));
            if (effective){
<<<<<<< Updated upstream:order/src/main/java/cn/edu/xmu/order/util/factory/PostOrderFactory.java
                return applicationContext.getBean("GroOrderService",PostOrderServiceImpl.class);
=======
<<<<<<< Updated upstream:order/src/main/java/cn/edu/xmu/order/factory/PostOrderFactory.java
                return GrouponOrderService.class;
=======
                return applicationContext.getBean("GroOrderService", PostOrderService.class);
>>>>>>> Stashed changes:order/src/main/java/cn/edu/xmu/order/util/factory/PostOrderFactory.java
>>>>>>> Stashed changes:order/src/main/java/cn/edu/xmu/order/factory/PostOrderFactory.java
            }
        }
        if (vo.getPresaleId()!=null){
            boolean effective=goodsService.checkPresaleSkuId(vo.getPresaleId(),skuIds.get(0));
            if (effective){
<<<<<<< Updated upstream:order/src/main/java/cn/edu/xmu/order/util/factory/PostOrderFactory.java
                return applicationContext.getBean("PreOrderService",PostOrderServiceImpl.class);
            }
        }
        return applicationContext.getBean("NorOrderService",PostOrderServiceImpl.class);
=======
<<<<<<< Updated upstream:order/src/main/java/cn/edu/xmu/order/factory/PostOrderFactory.java
                return PresaleOrderService.class;
            }
        }
        return NormalOrderService.class;
=======
                return applicationContext.getBean("PreOrderService", PostOrderService.class);
            }
        }
        return applicationContext.getBean("NorOrderService", PostOrderService.class);
>>>>>>> Stashed changes:order/src/main/java/cn/edu/xmu/order/util/factory/PostOrderFactory.java
>>>>>>> Stashed changes:order/src/main/java/cn/edu/xmu/order/factory/PostOrderFactory.java
    }
}
