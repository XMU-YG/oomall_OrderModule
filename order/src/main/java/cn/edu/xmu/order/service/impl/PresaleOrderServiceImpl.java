package cn.edu.xmu.order.service.impl;

import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.bo.OrderGoods;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.vo.OrderItemVo;
import cn.edu.xmu.order.model.vo.OrderVo;
import cn.edu.xmu.order.service.OrderService;
import cn.edu.xmu.order.util.OrderStatus;
import cn.edu.xmu.order.util.PostOrderService;
import cn.edu.xmu.produce.IFreightService;
import cn.edu.xmu.produce.IGoodsService;
import cn.edu.xmu.produce.IOtherService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 预售订单创建服务
 */
@Component("PreOrderService")
public class PresaleOrderServiceImpl implements PostOrderService {

    @Autowired
    private OrderService orderService;

    @DubboReference
    private IGoodsService goodsService;

    @DubboReference
    private IOtherService otherService;

    @DubboReference
    private IFreightService freightService;

    @Override
    public ReturnObject addNewOrderByCustomer(Long customerId, OrderVo vo) {
        ReturnObject<String> returnObject = null;
        OrderPo orderPo = vo.createOrderPo();
        orderPo.setGmtCreate(LocalDateTime.now());
        //预售团购只有一个OrderItem
        OrderItemVo orderItemVo = vo.getOrderItems().get(0);
        OrderItemPo orderItemPo = vo.createOrderItemsPo().get(0);

        String orderSn = Common.genSeqNum();
        orderPo.setOrderSn(orderSn);
        //0普通 1团购 2预售
        orderPo.setOrderType((byte) 2);
        //检查库存
        String orderGoodsJson = goodsService.findGoodsBySkuId(orderItemVo.getSkuId());
        OrderGoods order_goods = JacksonUtil.toObj(orderGoodsJson, OrderGoods.class);
        if (order_goods == null || orderItemVo.getQuantity() > order_goods.getQuantity()) {
            //库存不足
            return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
        }
        orderPo.setShopId(order_goods.getShopId());
        //构造OrderItemPo
        orderItemPo.setCouponActivityId(orderItemVo.getCouponActId());
        orderItemPo.setQuantity(orderItemVo.getQuantity());
        orderItemPo.setName(order_goods.getName());
        orderItemPo.setPrice(order_goods.getPrice());
        orderItemPo.setGoodsSkuId(order_goods.getGoods_sku_id());
        orderItemPo.setGmtCreate(LocalDateTime.now());
        orderItemPo.setBeShareId(otherService.getBeSharedId(orderItemPo.getGoodsSkuId(), customerId));
        orderItemPo.setGmtModified(LocalDateTime.now());
        //商品数量属性设为购买数量，方便之后处理
        order_goods.setQuantity(orderItemVo.getQuantity());
        ArrayList<OrderGoods> norGoodsArrayList=new ArrayList<>();
        norGoodsArrayList.add(order_goods);
        //处理购买的普通商品：扣库存
        ReturnObject nor = orderService.disposeNorGoodsList(norGoodsArrayList, (byte) 2);

        if (!nor.getCode().equals(ResponseCode.OK)) {
            //库存不足
            return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
        }
        //处理OrderPo
        orderPo.setCustomerId(customerId);
        //所有商品skuId与quantity的Map，用于计算运费
        Map<Long,Integer> goodsMap=new HashMap<>();
        goodsMap.put(orderItemPo.getGoodsSkuId(),orderItemPo.getQuantity());
        //计算运费
        orderPo.setFreightPrice(freightService.calculateFreight(orderPo.getRegionId(), goodsMap));
        //计算返点数
        String orderItemPosJson = JacksonUtil.toJson(orderItemPo);
        orderPo.setRebateNum(otherService.calculateRebateNum(orderItemPosJson, customerId));

        //设为待支付状态
        orderPo.setState((byte) OrderStatus.WAIT_FOR_PAID.getCode());
        //子状态为新订单
        orderPo.setSubstate((byte) OrderStatus.NEW_ORDER.getCode());
        //预售订单第一次总价是预售之和
        Long advancePrice=goodsService.getAdvancePrice(orderPo.getPresaleId(),orderItemPo.getGoodsSkuId())*orderItemPo.getQuantity();
        orderPo.setOriginPrice(advancePrice);
        orderPo.setGmtModified(LocalDateTime.now());
        //OrderPo写入数据库，返回orderId
        ReturnObject<Long> orderRet = orderService.insertOrder(orderPo);
        Long orderId = orderRet.getData();
        if (orderRet.getCode().equals(ResponseCode.OK)) {
            orderItemPo.setOrderId(orderId);
            //写入
            ReturnObject object = orderService.insertOrderItem(orderItemPo);
            if (!object.getCode().equals(ResponseCode.OK)) {
                return object;
            }
            returnObject = new ReturnObject(orderPo.getOrderSn());
        } else {
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        return returnObject;
    }

}
