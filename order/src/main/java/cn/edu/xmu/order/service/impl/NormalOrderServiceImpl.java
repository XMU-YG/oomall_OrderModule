package cn.edu.xmu.order.service.impl;

import cn.edu.xmu.goodsprovider.flashsale.FlashService;
import cn.edu.xmu.goodsprovider.goods.GoodsInner;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.bo.OrderItem;
//import cn.edu.xmu.order.service.RocketMQService;
import cn.edu.xmu.order_provider.IFreightService;
import cn.edu.xmu.goodsprovider.Module.OrderGoods;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.vo.OrderVo;
import cn.edu.xmu.order.model.vo.OrderItemVo;
import cn.edu.xmu.order.service.OrderService;
//import cn.edu.xmu.order.service.time.TimeService;
import cn.edu.xmu.order.util.OrderStatus;
import cn.edu.xmu.order.util.OrderType;
import cn.edu.xmu.order.util.CreateOrderService;

import cn.edu.xmu.share.dubbo.ShareService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 普通订单创建服务
 */
@Component("NorOrderService")
public class NormalOrderServiceImpl implements CreateOrderService {
//
//    @Autowired
//    private RocketMQService rocketMQService;

    @Autowired
    private OrderService orderService;

//    @Autowired
//    private TimeService timeService;

    @DubboReference(version ="0.0.1",check = false)
    private GoodsInner goodsInner;

    @DubboReference(version ="0.0.1",check = false)
    private FlashService flashService;

    @DubboReference(version = "0.0.1",check = false)
    private ShareService shareService;

    @DubboReference(version = "0.0.1",check = false)
    private IFreightService freightService;

    @Override
    public ReturnObject createOrderByCustomer(Long customerId, OrderVo vo) throws JsonProcessingException, InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        ReturnObject<String> returnObject=null;
        OrderPo orderPo=vo.createOrderPo();
        orderPo.setGmtCreate(LocalDateTime.now());
        List<OrderItemVo> orderItemVos=vo.getOrderItems();
        List<OrderItemPo> orderItemPos=new ArrayList<>(orderItemVos.size());

        List<OrderItem> orderItemList=new ArrayList<>(orderItemPos.size());

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
            String orderGoodsJson=flashService.findGoodsBySkuId(orderItemVo.getSkuId());
            //System.out.println(orderItemVo.getSkuId()+"  "+orderGoodsJson);
            OrderGoods order_goods= JacksonUtil.toObj(orderGoodsJson,OrderGoods.class);
            //System.out.println(order_goods.getGoods_sku_id()+"  "+order_goods.getQuantity());
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
            orderItemPo.setBeShareId(shareService.fillOrderItemByBeShare(orderItemPo.getGoodsSkuId(), customerId));
            orderItemPos.add(orderItemPo);
            //用来算优惠
            OrderItem orderItem=new OrderItem(orderItemPo);
            orderItemList.add(orderItem);
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
        ReturnObject nor=orderService.disposeNorGoodsList(0L,norGoodsArrayList,"NorOrderService");
        //处理购买的秒杀商品：扣库存
        ReturnObject sec=orderService.disposeSecGoodsList(secGoodsArrayList);
        if (!nor.getCode().equals(ResponseCode.OK)||!sec.getCode().equals(ResponseCode.OK)){
            //库存不足
            return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
        }
        //处理OrderPo
        orderPo.setCustomerId(customerId);
        //计算运费
        orderPo.setFreightPrice(freightService.calculateFreight(orderPo.getRegionId(),goodsMap));
        //计算返点数
        String orderItemPosJson=JacksonUtil.toJson(orderItemPos);
        //todo otherService.calculateRebateNum(orderItemPosJson,customerId)
        orderPo.setRebateNum(0);

        //计算订单优惠，注意在函数中要设置OrderGoods的discount属性
//        orderItemList=orderService.calculateDiscount(orderItemList);
//        for (OrderItemPo o:orderItemPos) {
//            orderItemList.forEach(v->{
//                if (o.getId().equals(v.getId())){
//                    o.setDiscount(v.getDiscount());
//                }
//            });
//        }
        orderPo.setDiscountPrice(0L);
        //设为待支付状态
        orderPo.setState((byte) OrderStatus.WAIT_FOR_PAID.getCode());
        orderPo.setSubstate((byte) OrderStatus.NEW_ORDER.getCode());
        //计算原价
        orderPo.setOriginPrice(orderService.calculateOriginPrice(orderItemList));
        orderPo.setGmtModified(LocalDateTime.now());
        //OrderPo写入数据库，返回orderId
        ReturnObject<Long> orderRet=orderService.insertOrder(orderPo);
        Long orderId=orderRet.getData();
        //rocketMQService.sendOrderPayMessage(orderId);
        if (orderRet.getCode().equals(ResponseCode.OK)){
            /*构造orderItems*/
            for (OrderItemPo orderItemPo:orderItemPos) {
                orderItemPo.setOrderId(orderId);
                orderItemPo.setGmtModified(LocalDateTime.now());
                //写入
                ReturnObject object=orderService.insertOrderItem(orderItemPo);
                if (!object.getCode().equals(ResponseCode.OK)){
                    return object;
                }
            }
            returnObject=new ReturnObject(orderPo.getOrderSn());
            System.out.println("Sn: "+orderPo.getOrderSn());
        }
        else {
            returnObject=new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        return returnObject;
    }

    @Override
    public boolean deductStock(Long actId,Long skuId, Integer quantity) {
        return goodsInner.deductNorStock(skuId, quantity);
    }


}
