package cn.edu.xmu.order.service.impl;

import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.bo.OrderGoods;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.vo.OrderVo;
import cn.edu.xmu.order.model.vo.OrderItemVo;
import cn.edu.xmu.order.service.OrderService;
import cn.edu.xmu.order.util.OrderStatus;
import cn.edu.xmu.order.util.OrderType;
import cn.edu.xmu.order.util.PostOrderService;
import cn.edu.xmu.produce.freight.IFreightService;
import cn.edu.xmu.produce.goods.IGoodsService;
import cn.edu.xmu.produce.other.IOtherService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 普通订单创建服务
 */
@Component("NorOrderService")
public class NormalOrderServiceImpl implements PostOrderService {

    @Autowired
    private OrderService orderService;

    @DubboReference(version ="1.0-SNAPSHOT")
    private IGoodsService goodsService;

    @DubboReference(version ="1.0-SNAPSHOT")
    private IOtherService otherService;

    @DubboReference(version ="1.0-SNAPSHOT")
    private IFreightService freightService;

    @Override
    public ReturnObject addNewOrderByCustomer(Long customerId, OrderVo vo) {
        ReturnObject<String> returnObject=null;
        OrderPo orderPo=vo.createOrderPo();
        orderPo.setGmtCreate(LocalDateTime.now());
        List<OrderItemVo> orderItemVos=vo.getOrderItems();
        List<OrderItemPo> orderItemPos=vo.createOrderItemsPo();

        String orderSn= Common.genSeqNum();
        orderPo.setOrderSn(orderSn);
        //0普通 1团购 2预售
        orderPo.setOrderType(OrderType.NORMAL.getCode());
        //普通商品
        ArrayList<OrderGoods> norGoodsArrayList=new ArrayList<>();
        //秒杀商品
        ArrayList<OrderGoods> secGoodsArrayList=new ArrayList<>();
        //所有商品skuId与quantity的Map，用于计算运费
        Map<Long,Integer> goodsMap=new HashMap<>(orderItemPos.size());
        //检查库存
        for (OrderItemVo orderItemVo:orderItemVos) {
            OrderItemPo orderItemPo=new OrderItemPo();
            String orderGoodsJson=goodsService.findGoodsBySkuId(orderItemVo.getSkuId());
            OrderGoods order_goods= JacksonUtil.toObj(orderGoodsJson,OrderGoods.class);
            if (order_goods==null||orderItemVo.getQuantity()>order_goods.getQuantity()){
                //库存不足
                return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
            }
            //构造OrderItemPo
            orderItemPo.setCouponActivityId(orderItemVo.getCouponActId());
            orderItemPo.setQuantity(orderItemVo.getQuantity());
            orderItemPo.setName(order_goods.getName());
            orderItemPo.setPrice(order_goods.getPrice());
            orderItemPo.setGoodsSkuId(order_goods.getGoods_sku_id());
            orderItemPo.setGmtCreate(LocalDateTime.now());
            orderItemPo.setBeShareId(otherService.getBeSharedId(orderItemPo.getGoodsSkuId(),customerId));
            orderItemPo.setGmtModified(LocalDateTime.now());
            orderItemPos.add(orderItemPo);

            //商品数量属性设为购买数量，方便之后处理
            order_goods.setQuantity(orderItemVo.getQuantity());
            //将商品分类（普通和秒杀）
            if (order_goods.isSeckill()){
                secGoodsArrayList.add(order_goods);
            }else{
                norGoodsArrayList.add(order_goods);
            }
            goodsMap.put(orderItemPo.getGoodsSkuId(),orderItemPo.getQuantity());
        }
        //处理购买的普通商品：扣库存
        ReturnObject nor=orderService.disposeNorGoodsList(norGoodsArrayList,OrderType.NORMAL.getCode());
        //处理购买的秒杀商品：扣库存
        ReturnObject sec=orderService.disposeSecGoodsList(secGoodsArrayList);
        if (!nor.getCode().equals(ResponseCode.OK)||!sec.getCode().equals(ResponseCode.OK)){
            //库存不足
            return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
        }
        //处理OrderPo
        orderPo.setCustomerId(customerId);
        //计算运费
        String itemJson=JacksonUtil.toJson(goodsMap);
        orderPo.setFreightPrice(freightService.calculateFreight(orderPo.getRegionId(),goodsMap));
        //计算返点数
        String orderItemPosJson=JacksonUtil.toJson(orderItemPos);
        orderPo.setRebateNum(otherService.calculateRebateNum(orderItemPosJson,customerId));

        /*todo 计算订单优惠，注意在函数中要设置OrderGoods的discount属性*/
        //orderPo.setDiscountPrice(orderService.calculateDiscount());
        //设为待支付状态
        orderPo.setState((byte) OrderStatus.WAIT_FOR_PAID.getCode());
        //计算原价
        orderPo.setOriginPrice(orderService.calculateOriginPrice(orderItemPos));
        orderPo.setGmtModified(LocalDateTime.now());
        //OrderPo写入数据库，返回orderId
        ReturnObject<Long> orderRet=orderService.insertOrder(orderPo);
        Long orderId=orderRet.getData();
        if (orderRet.getCode().equals(ResponseCode.OK)){
            /*构造orderItems*/
            for (OrderItemPo orderItemPo:orderItemPos) {
                orderItemPo.setOrderId(orderId);
                //写入
                ReturnObject object=orderService.insertOrderItem(orderItemPo);
                if (!object.getCode().equals(ResponseCode.OK)){
                    return object;
                }
            }
            returnObject=new ReturnObject(orderPo.getOrderSn());
        }
        else {
            returnObject=new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        return returnObject;
    }


}
