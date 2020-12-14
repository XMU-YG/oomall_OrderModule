package cn.edu.xmu.order.service;

import cn.edu.xmu.ooad.model.VoObject;

import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.JacksonUtil;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.dao.OrderDao;
import cn.edu.xmu.order.dao.OrderItemDao;

import cn.edu.xmu.order.model.bo.Customer;
import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.bo.Shop;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.vo.AddressVo;

import com.github.pagehelper.PageInfo;


import cn.edu.xmu.order.model.bo.*;
import cn.edu.xmu.order.model.vo.OrderItemVo;
import cn.edu.xmu.order.model.vo.OrderVo;
import cn.edu.xmu.order.util.OrderStatus;
import cn.edu.xmu.produce.goods.IGoodsService;
import cn.edu.xmu.produce.other.IOtherService;
import org.apache.dubbo.config.annotation.DubboReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单模块服务层
 * @author Gang Ye
 */
@Service
public class OrderService {
    private Logger logger=LoggerFactory.getLogger(OrderService.class);


    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private OrderDao orderDao;

    @DubboReference(version ="1.0-SNAPSHOT")
    private IGoodsService goodsService;

    @DubboReference(version ="1.0-SNAPSHOT")
    private IOtherService otherService;

    public List<OrderItemPo> findOrderItemsByOrderId(Long orderId){
        return orderItemDao.getOrderItemsByOrderId(orderId);
    }

    public List<OrderItemPo> getItemsBySkuId(Long skuId) {
        return orderItemDao.getItemsBySkuId(skuId);
    }

    public ReturnObject insertOrderItem(OrderItemPo orderItemPo) {
        return orderItemDao.insertOrderItem(orderItemPo);
    }

    @Transactional
    public ReturnObject<PageInfo<VoObject>> getAllSimpleOrders(Long customerId, String orderSn, Integer state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize){
        return orderDao.getAllSimpleOrders(customerId,orderSn,state,beginTime,endTime,page,pageSize);
    }

    @Transactional

    public ReturnObject<VoObject> getCusOrderById(Long customerId, Long orderId){
        ReturnObject<VoObject> returnObject= orderDao.getOrderById(customerId,orderId);

        if (returnObject.getCode().equals(ResponseCode.OK)){

            Order order=(Order)returnObject.getData();

            Customer customer=new Customer();
            customer.setCustomerId(order.getCustomer().getCustomerId());


            Shop shop=new Shop();
            shop.setShopId(order.getShop().getShopId());

            order.setCustomer(customer);
            order.setShop(shop);

            return new ReturnObject<>(order);
        }
        else{
            return returnObject;
        }

    }

    @Transactional
    public ReturnObject modifySelfOrderAddressById(Long customerId, Long orderId, AddressVo vo){
        return orderDao.modifySelfOrderAddressById(customerId,orderId,vo);
    }

    @Transactional
    public ReturnObject deleteSelfOrderById(Long customerId, Long orderId) {
        return orderDao.deleteSelfOrderById(customerId,orderId);
    }

    @Transactional
    public ReturnObject confirmSelfOrderById(Long customerId, Long orderId) {
        return orderDao.confirmSelfOrderById(customerId,orderId);
    }

    @Transactional
    public ReturnObject translateGroToNor(Long customerId, Long id) {
        return orderDao.transLateGroToNor(customerId,id);
    }

    @Transactional
    public ReturnObject<PageInfo<VoObject>> getShopSelfSimpleOrders(Long shopId, Long customerId, String orderSn, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        return orderDao.getShopSelfSimpleOrders(shopId,customerId,orderSn,beginTime,endTime,page,pageSize);
    }

    @Transactional
    public ReturnObject<VoObject> getShopSelfOrder(Long shopId, Long id) {
        ReturnObject<VoObject> returnObject= orderDao.getShopSelfOrder(shopId,id);

        if (returnObject.getCode().equals(ResponseCode.OK)){
            Order order=(Order)returnObject.getData();

            Customer customer=new Customer();
            customer.setCustomerId(order.getCustomer().getCustomerId());

            Shop shop=new Shop();
            shop.setShopId(order.getShop().getShopId());

            order.setCustomer(customer);
            order.setShop(shop);
            return new ReturnObject<>(order);
        }
        else{
            return returnObject;
        }

    }

    @Transactional
    public ReturnObject modifyOrderMessage(Long shopId, Long orderId, String message) {
        return orderDao.modifyOrderMessage(shopId,orderId,message);
    }

    @Transactional
    public ReturnObject deleteShopOrder(Long shopId, Long orderId) {
        return orderDao.deleteShopOrder(shopId,orderId);
    }

    @Transactional()
    public ReturnObject deliverShopOrder(Long shopId, Long orderId, String freightSn) {
        return orderDao.deliverShopOrder(shopId,orderId,freightSn);
    }

    /**
     * 订单写入
     * @param orderPo
     * @return 订单id
     * @author Gang Ye
     */
    @Transactional
    public ReturnObject<Long> insertOrder(OrderPo orderPo){
        return orderDao.insertOrder(orderPo);
    }

    /**
     * 判断订单是否属于该顾客
     * @param customerId
     * @param orderId
     * @return
     */
    public boolean verifyOrderByCustomerId(Long customerId,Long orderId) {

        OrderPo ret=orderDao.getOrderPoById(orderId);
        if (ret.getCustomerId().equals(customerId)){
            return true;
        }
        return false;
    }

    /**
     * 判断订单是否属于该店铺
     * @param shopId
     * @param orderId
     * @return
     */
    public boolean verifyOrderByShopId(Long shopId,Long orderId) {
        ReturnObject<VoObject> returnObject=orderDao.getShopSelfOrder(shopId,orderId);
        if (returnObject.getCode().equals(ResponseCode.OK)){
            return true;
        }
        return false;
    }


    /**
     * 处理普通商品订单明细
     * 扣库存(普通商品)
     * @param goodsList 商品列表
     * @return 是否成功
     * @modified by Gang Ye  修改业务只有扣库存
     */
    @Transactional
    public ReturnObject disposeNorGoodsList(List<OrderGoods> goodsList, Byte type)  {
        //扣库存
        //存储以扣库存的商品skuId和quantity，便于回滚,局域变量，线程安全
        Map<Long,Integer> map=new HashMap<>(goodsList.size());
        for (OrderGoods po:goodsList) {
            //扣库存
            boolean deductSuccessful=goodsService.deductStock(po.getGoods_sku_id(),po.getQuantity(),type);
            if (!deductSuccessful) {
                //库存不足
                //库存回滚
                map.forEach((k,v)->{
                    goodsService.deductStock(k,-v,type);
                });
                return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
            }
            //扣成功则存入map
            map.put(po.getGoods_sku_id(),po.getQuantity());
        }
        return new ReturnObject<>(ResponseCode.OK);
    }

    /**
     * 处理秒杀商品订单明细
     * 扣库存(秒杀商品)
     * @param goodsList 商品列表
     * @return 是否成功
     * @modified by Gang Ye  修改业务只有扣库存
     */
    @Transactional
    public ReturnObject disposeSecGoodsList(List<OrderGoods> goodsList) {
        //扣库存
        //存储以扣库存的商品skuId和quantity，便于回滚,局域变量，线程安全
        Map<Long,Integer> map=new HashMap<>(goodsList.size());
        for (OrderGoods po:goodsList) {
            //扣库存
            boolean deductSuccessful=this.deductStock(po.getGoods_sku_id(),po.getQuantity());
            if (!deductSuccessful) {
                //库存不足
                //库存回滚
                map.forEach((k,v)->{
                    this.deductStock(k,-v);
                });
                return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
            }

            //扣成功则存入map
            map.put(po.getGoods_sku_id(),po.getQuantity());
        }
        return new ReturnObject<>(ResponseCode.OK);
    }

    /**
     * 计算优惠数额
     * @return
     */
    public Long calculateDiscount(List<OrderItemPo> orderItemPos){
        Long sum=0L;
        for (OrderItemPo po:orderItemPos
             ) {
            sum+=po.getDiscount();
        }
        return sum;
    }

    /**
     * 查询sn有否存在
     * @param orderSn
     * @return
     */
    public boolean haveOrderSn(String orderSn){
        return orderDao.haveOrderSn(orderSn);
    }

    /**
     * 计算原价
     * @param orderGoods
     * @return
     */
    public Long calculateOriginPrice(List<OrderItemPo> orderGoods){
        Long sum=0l;
        for (OrderItemPo o:orderGoods) {
            sum+=o.getPrice()*o.getQuantity();
        }
        return sum;
    }


    /**
     * 秒杀扣库存
     * @param skuId
     * @param quantity
     * @return
     * @modified by Gang Ye  删除秒杀商品在redis上找不到的情况，即默认所有都在redis上
     */
    @Transactional
    public boolean deductStock(Long skuId, Integer quantity) {
        int flag=orderDao.deductStock(skuId,quantity);
        switch (flag){
            case 1:
                return true;
            case 0:
                return false;
        }
        return false;
    }


    /**
     * 创建售后订单
     * @param shopId 发起售后的店铺id
     * @param orderVoJson 新订单信息视图Json
     * @return
     * @author Gang Ye
     */
    public ReturnObject createAfterSaleOrder(Long shopId, String orderVoJson) {
        OrderVo vo=JacksonUtil.toObj(orderVoJson,OrderVo.class);
        ReturnObject returnObject=null;
        assert vo != null;
        OrderPo orderPo=vo.createOrderPo();
        orderPo.setGmtCreate(LocalDateTime.now());
        List<OrderItemVo> orderItemVos=vo.getOrderItems();
        List<OrderItemPo> orderItemPos=vo.createOrderItemsPo();

        String orderSn= Common.genSeqNum();
        orderPo.setOrderSn(orderSn);
        //orderPo.setOrderType((byte) 0);
        //普通商品
        ArrayList<OrderGoods> norGoodsArrayList=new ArrayList<>();
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
            orderItemPo.setQuantity(orderItemVo.getQuantity());
            orderItemPo.setName(order_goods.getName());
            orderItemPo.setPrice(order_goods.getPrice());
            orderItemPo.setGoodsSkuId(order_goods.getGoods_sku_id());
            orderItemPo.setGmtCreate(LocalDateTime.now());
            orderItemPo.setGmtModified(LocalDateTime.now());
            orderItemPos.add(orderItemPo);

            //商品数量属性设为购买数量，方便之后处理
            order_goods.setQuantity(orderItemVo.getQuantity());
            norGoodsArrayList.add(order_goods);
        }
        //处理购买的普通商品：扣库存
        ReturnObject nor=this.disposeNorGoodsList(norGoodsArrayList,(byte)0);
        if (!nor.getCode().equals(ResponseCode.OK)){
            //库存不足
            return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
        }
        //处理OrderPo
        orderPo.setShopId(shopId);
        //设为待支付状态
        orderPo.setState((byte) OrderStatus.SHIPPED.getCode());
        //计算原价
        orderPo.setOriginPrice(0L);
        orderPo.setGmtModified(LocalDateTime.now());
        //OrderPo写入数据库，返回orderId
        ReturnObject<Long> orderRet=this.insertOrder(orderPo);
        Long orderId=orderRet.getData();
        if (orderRet.getCode().equals(ResponseCode.OK)){
            /*构造orderItems*/
            for (OrderItemPo orderItemPo:orderItemPos) {
                orderItemPo.setOrderId(orderId);
                //写入
                ReturnObject object=this.insertOrderItem(orderItemPo);
                if (!object.getCode().equals(ResponseCode.OK)){
                    return object;
                }
            }
            returnObject=new ReturnObject(ResponseCode.OK);
        }
        else {
            returnObject=new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        return returnObject;
    }

    /**
     * 分单
     * @param orderId 需要分的订单
     * @author Gang Ye
     * @created 2020/12/10 2:22
     * @modified Gang Ye
     */
    public void classifyOrder(Long orderId){

        OrderPo orderPo=orderDao.getOrderPoById(orderId);
        List<OrderItemPo> orderItems=this.findOrderItemsByOrderId(orderId);

        Map<Long,List<OrderItemPo>> goodsMapByShop=new HashMap<>(orderItems.size());
        for (OrderItemPo orderItem :orderItems) {
            String goodsJson=goodsService.findGoodsBySkuId(orderItem.getGoodsSkuId());
            OrderGoods goods= JacksonUtil.toObj(goodsJson,OrderGoods.class);
            assert goods != null;
            goodsMapByShop.computeIfAbsent(goods.getShopId(), k->new ArrayList<>()).add(orderItem);

        }
        //分单
        goodsMapByShop.forEach((k,v)->{
            //子订单
            OrderPo po=new OrderPo();

            po.setGmtCreate(LocalDateTime.now());
            po.setOrderSn(Common.genSeqNum());
            po.setShopId(k);
            po.setPid(orderId);
            po.setCustomerId(orderPo.getCustomerId());
            po.setOrderType(orderPo.getOrderType());
            po.setState((byte)OrderStatus.WAIT_FOR_RECEIVE.getCode());
            po.setOriginPrice(this.calculateOriginPrice(v));
            po.setFreightPrice(orderPo.getFreightPrice()*(po.getOriginPrice()/orderPo.getOriginPrice()));
            po.setDiscountPrice(this.calculateDiscount(v));
            po.setRebateNum(otherService.calculateRebateNum(JacksonUtil.toJson(v),po.getCustomerId()));
            po.setBeDeleted(orderPo.getBeDeleted());
            po.setCouponId(orderPo.getCouponId());
            po.setGmtModified(LocalDateTime.now());
            //写入
            Long id=orderDao.insertOrder(orderPo).getData();
            v.forEach(x->{
                OrderItemPo orderItemPo=orderItemDao.getOrderItemById(x.getId());
                orderItemPo.setOrderId(id);
                orderItemPo.setGmtModified(LocalDateTime.now());
                //更新
                orderItemDao.updateOrderItem(orderItemPo);
            });
        });
    }
    public List<Long> getOrderItemIdList(List<Long> skuIds,Long customerId){
        List<Long> orderItemIds=new ArrayList<>(skuIds.size());

        for (Long skuId:skuIds) {
            List<OrderItemPo> orderItemPos=this.getItemsBySkuId(skuId);
            orderItemPos.forEach(v->{
                if (this.verifyOrderByCustomerId(customerId,v.getOrderId())){
                    orderItemIds.add(v.getId());
                }
            });
        }
        return orderItemIds;
    }

    public OrderItemPo getOrderItemById(Long orderItemId) {
        return orderItemDao.getOrderItemById(orderItemId);
    }

    public OrderPo getOrderByItemId(Long orderItemId) {
        OrderItemPo orderItemPo=this.getOrderItemById(orderItemId);
        return orderDao.getOrderPoById(orderItemPo.getOrderId());
    }
}
