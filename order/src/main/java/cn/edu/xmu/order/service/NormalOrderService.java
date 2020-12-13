package cn.edu.xmu.order.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.bo.OrderItem;
import cn.edu.xmu.order.model.bo.SimpleOrderItem;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.vo.NewOrderVo;
import cn.edu.xmu.order.service.impl.OrderServiceImpl;
import cn.edu.xmu.order.service.impl.OtherServiceImpl;
import cn.edu.xmu.order.service.impl.PostOrderServiceImpl;
import cn.edu.xmu.order.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 普通订单创建服务
 */
@Component("NorOrderService")
public class NormalOrderService implements PostOrderServiceImpl {

    @Autowired
    private OrderService orderService;

    @Override
    public ReturnObject<VoObject> addNewOrderByCustomer(Long customerId, NewOrderVo vo) {
        ReturnObject<VoObject> returnObject=null;
        //父订单
        OrderPo orderPo=vo.createOrderPo();
        String orderSn= Common.genSeqNum();
        while(!orderService.haveOrderSn(orderSn)){
            orderSn=Common.genSeqNum();
        }
        orderPo.setOrderSn(orderSn);

        orderPo.setOrderType((byte) 0);
        orderPo.setState((byte)OrderStatus.ORDER_CREATED.getCode());

        ReturnObject<List<OrderItem>> listReturnObject=orderService.disposeNorOrderItemsPo(vo.createOrderItemsPo());
        if (listReturnObject.getCode().equals(ResponseCode.SKU_NOTENOUGH)){
            return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
        }

        List<OrderItemPo> orderItems=listReturnObject.getData();

        orderPo.setCustomerId(customerId);

//        OrderServiceImpl freightService=null;
//        orderPo.setFreightPrice(freightService.calculateFreight(orderItems));

//        OtherServiceImpl otherService=null;
//        orderPo.setRebateNum(otherService.calculateRebateNum(orderItems,customerId));

        /*计算订单优惠，注意在函数中要设置OrderGoods的discount属性*/
        //orderPo.setDiscountPrice(orderService.calculateDiscount());

        orderPo.setState((byte) OrderStatus.UNPAID.getCode());
        orderPo.setGmtCreate(LocalDateTime.now());
        orderPo.setOriginPrice(orderService.calculateOriginPrice(orderItems));

        ReturnObject<Long> orderRet=orderService.insertOrder(orderPo);

        ArrayList<SimpleOrderItem> simpleOrderItems=new ArrayList<>(vo.createOrderItemsPo().size());
        ArrayList<OrderItemPo> orderItemPoArrayList=new ArrayList<>(vo.createOrderItemsPo().size());
        if (orderRet.getCode().equals(ResponseCode.OK)){
            Order order=new Order(orderPo);
            /*构造orderItems*/
            for (OrderItemPo orderItemPo:orderItems) {

                orderItemPo.setCouponId(orderPo.getCouponId());
                orderItemPo.setOrderId(orderRet.getData());
                orderItemPo.setGmtCreate(LocalDateTime.now());
                ReturnObject object=orderService.insertOrderItem(orderItemPo);
                orderItemPoArrayList.add(orderItemPo);

                SimpleOrderItem simpleOrderItem=new SimpleOrderItem(orderItemPo);
                simpleOrderItems.add(simpleOrderItem);

                if (!object.getCode().equals(ResponseCode.OK)){
                    return object;
                }
            }
            order.setSimpleOrderItemList(simpleOrderItems);
            returnObject =new ReturnObject<>(order);
        }
        else {
            returnObject=new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        return returnObject;
    }

}
