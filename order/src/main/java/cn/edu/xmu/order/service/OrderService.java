package cn.edu.xmu.order.service;

import cn.edu.xmu.ooad.model.VoObject;

import cn.edu.xmu.ooad.order.bo.COrderItem;
import cn.edu.xmu.ooad.order.discount.BaseCouponDiscount;
import cn.edu.xmu.ooad.order.discount.BaseCouponLimitation;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.JacksonUtil;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.dao.OrderDao;
import cn.edu.xmu.order.dao.OrderItemDao;

import cn.edu.xmu.order.util.PostOrderService;
import cn.edu.xmu.order_provider.goods.modol.OrderGoods;
import cn.edu.xmu.order_provider.model.OtherDTO;
import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.vo.AddressVo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import cn.edu.xmu.order.model.bo.*;
import cn.edu.xmu.order.model.vo.OrderItemVo;
import cn.edu.xmu.order.model.vo.OrderVo;
import cn.edu.xmu.order.util.OrderStatus;
import cn.edu.xmu.order_provider.goods.IGoodsService;
import cn.edu.xmu.order_provider.other.IOtherService;
import org.apache.dubbo.config.annotation.DubboReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
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

    @Autowired
    private ApplicationContext applicationContext;

    @DubboReference
    private IGoodsService goodsService;

    @DubboReference
    private IOtherService otherService;

    /**
     * 根据订单号查询订单明细
     * @param orderId
     * @return
     */
    public List<OrderItem> findOrderItemsByOrderId(Long orderId){
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

    /**
     * 顾客查询本人订单详情
     * @param customerId
     * @param orderId
     * @return Order视图
     * @author Gang Ye
     */
    @Transactional
    public ReturnObject<VoObject> getCusOrderById(Long customerId, Long orderId){

        ReturnObject returnObject= orderDao.getOrderById(orderId);
        if (returnObject.getCode().equals(ResponseCode.OK)){
            Order order=(Order) returnObject.getData();
            if(order.getCustomer().getCustomerId().equals(customerId)){
                //构造完整Order详情
                List<SimpleOrderItem> orderItems=orderItemDao.getSimOrderItemsByOrderId(order.getId());
                order.setSimpleOrderItemList(orderItems);
                Long shopId=order.getShop().getShopId();
                //Customer customer=JacksonUtil.toObj(otherService.findCustomerById(customerId),Customer.class);
                //Shop shop=JacksonUtil.toObj(goodsService.findShopById(shopId),Shop.class);
                //order.setCustomer(customer);
                //order.setShop(shop);
                return new ReturnObject<>(order);
            }
            else{
                logger.debug("customer getOrderById error: don't have privilege!   orderId:  "+orderId+"   customerId:  "+customerId);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权访问，不是自己订单");
            }

        }
        else{
            return returnObject;
        }
    }

    /**
     * 修改订单地址
     * @param customerId
     * @param orderId
     * @param vo 新信息
     * @return
     * @author Gang Ye
     */
    @Transactional
    public ReturnObject modifySelfOrderAddressById(Long customerId, Long orderId, AddressVo vo){
        return orderDao.modifySelfOrderAddressById(customerId,orderId,vo);
    }

    /**
     * 顾客取消或者删除订单
     * @param customerId
     * @param orderId
     * @return
     */
    @Transactional
    public ReturnObject deleteSelfOrderById(Long customerId, Long orderId) {
        ReturnObject returnObject=orderDao.deleteOrderByCus(customerId,orderId);
        if (returnObject.getErrmsg().equals("已取消")){
            List<SimpleOrderItem> simpleOrderItems=orderItemDao.getSimOrderItemsByOrderId(orderId);
            for (SimpleOrderItem item:simpleOrderItems) {
                goodsService.addStock(item.getGoods_sku_id(),item.getQuantity());
            }
        }
        return returnObject;
    }
    /**
     * 顾客确认收货
     * @param customerId
     * @param orderId
     * @return
     */
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

    /**
     * 店铺根据订单号查询订单详情
     * @param shopId
     * @param id
     * @return Order
     * @Gang Ye
     */
    @Transactional
    public ReturnObject<VoObject> getShopSelfOrder(Long shopId, Long id) {
        ReturnObject object= orderDao.getOrderById(id);
        ReturnObject returnObject=null;
        if (object.getCode().equals(ResponseCode.OK)){
            Order order=(Order) object.getData();
            if(shopId==0L||order.getShop().getShopId().equals(shopId)){
                logger.debug("shop getSelfOrder success！  orderId:  "+id+"   shopId:  "+shopId);
                //构造完整Order详情
                List<SimpleOrderItem> orderItems=orderItemDao.getSimOrderItemsByOrderId(order.getId());
                order.setSimpleOrderItemList(orderItems);
                Long customerId=order.getCustomer().getCustomerId();
                //Customer customer=JacksonUtil.toObj(otherService.findCustomerById(customerId),Customer.class);
                if(shopId!=0){
                    //Shop shop=JacksonUtil.toObj(goodsService.findShopById(shopId),Shop.class);
                    //order.setShop(shop);
                }
                //order.setCustomer(customer);
                return new ReturnObject<>(order);
            } else{
                logger.debug("customer getOrderById error: don't have privilege!   orderId:  "+id+"  shopId:  "+shopId);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权访问，不是自己订单");
            }
        } else{
            return returnObject;
        }
    }

    /**
     * 店铺修改留言
     * @param shopId
     * @param orderId
     * @param message
     * @return
     */
    @Transactional
    public ReturnObject modifyOrderMessage(Long shopId, Long orderId, String message) {
        return orderDao.modifyOrderMessage(shopId,orderId,message);
    }
    @Transactional
    public ReturnObject deleteShopOrder(Long shopId, Long orderId) {
        return orderDao.deleteOrderByShop(shopId,orderId);
    }
    @Transactional
    public ReturnObject deliverShopOrder(Long shopId, Long orderId, String freightSn) {
        return orderDao.deliverOrderByShop(shopId,orderId,freightSn);
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
        ReturnObject<Order> ret=orderDao.getOrderById(orderId);
        if (ret.getData()==null){
            return false;
        }
        return ret.getData().getCustomer().getCustomerId().equals(customerId);
    }

    /**
     * 判断订单是否属于该店铺
     * @param shopId
     * @param orderId
     * @return
     */
    public boolean verifyOrderByShopId(Long shopId,Long orderId) {
        ReturnObject<Order> returnObject=orderDao.getOrderById(orderId);
        if (returnObject.getData()==null){
            return false;
        }
        return returnObject.getData().getShop().getShopId().equals(shopId);
    }

    /**
     * 处理普通商品订单明细
     * 扣库存(普通商品)
     * @param goodsList 商品列表
     * @return 是否成功
     * @modified by Gang Ye  修改业务只有扣库存
     */
    @Transactional
    public ReturnObject disposeNorGoodsList(List<OrderGoods> goodsList, String beanName)  {

        PostOrderService deductService=applicationContext.getBean(beanName,PostOrderService.class);
        //扣库存
        //存储以扣库存的商品skuId和quantity，便于回滚,局域变量，线程安全
        Map<Long,Integer> map=new HashMap<>(goodsList.size());
        for (OrderGoods po:goodsList) {
            //扣库存
            boolean deductSuccessful=deductService.deductStock(po.getGoods_sku_id(),po.getQuantity());
            if (!deductSuccessful) {
                //库存不足
                //库存回滚
                map.forEach((k,v)->{
                    deductService.deductStock(k,-v);
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
    public Long totalDiscount(List<OrderItem> orderItems){

        Long sum=0L;
        for (OrderItem po:orderItems
             ) {
            sum+=po.getDiscount();
        }
        return sum;
    }

    /**
     * 计算原价
     * @param orderGoods
     * @return
     */
    public Long calculateOriginPrice(List<OrderItem> orderGoods){
        long sum=0L;
        for (OrderItem o:orderGoods) {
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
     * 内部接口
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
        ReturnObject nor=this.disposeNorGoodsList(norGoodsArrayList,"NorOrderService");
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
     * 内部接口
     * 分单
     * @param orderId 需要分的订单
     * @author Gang Ye
     * @created 2020/12/10 2:22
     * @modified Gang Ye
     */
    public void classifyOrder(Long orderId){
        Order order=orderDao.getOrderById(orderId).getData();
        List<OrderItem> orderItems=this.findOrderItemsByOrderId(orderId);
        Map<Long,List<OrderItem>> goodsMapByShop=new HashMap<>(orderItems.size());
        for (OrderItem orderItem :orderItems) {
            String goodsJson=goodsService.findGoodsBySkuId(orderItem.getSkuId());
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
            po.setCustomerId(order.getCustomer().getCustomerId());
            po.setOrderType(order.getOrderType());
            po.setState((byte)OrderStatus.WAIT_FOR_RECEIVE.getCode());
            po.setOriginPrice(this.calculateOriginPrice(v));
            po.setFreightPrice(order.getFreightPrice()*(po.getOriginPrice()/order.getOriginPrice()));
            //po.setDiscountPrice(this.calculateDiscount(v));
            po.setRebateNum(otherService.calculateRebateNum(JacksonUtil.toJson(v),po.getCustomerId()));
            po.setCouponId(order.getCouponId());
            po.setGmtModified(LocalDateTime.now());
            Long id=orderDao.insertOrder(po).getData();
            v.forEach(x->{
                OrderItemPo orderItemPo=orderItemDao.getOrderItemPoById(x.getId());
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

    /**
     * 内部接口
     * @param orderItemId
     * @return
     */
    public OrderItem getOrderItemById(Long orderItemId) {
        return orderItemDao.getOrderItemById(orderItemId);
    }
//    public OrderPo getOrderByItemId(Long orderItemId) {
//        OrderItemPo orderItemPo=this.getOrderItemById(orderItemId);
//        return orderDao.getOrderPoById(orderItemPo.getOrderId());
//    }

    /**
     * 定时器调用任务
     * @param customerId
     * @param orderId
     */
    public void checkOrderPayState(Long customerId,Long orderId){
        Byte state=orderDao.getOrderState(orderId);
        if (state==OrderStatus.NEW_ORDER.getCode()){
            this.deleteSelfOrderById(customerId,orderId);
        }
    }

    public void checkOrderRebate(){
        List<SimpleOrder> simpleOrders=orderDao.getAllOrders();

    }

    /**
     * 内部接口
     * 为其他模块提供订单DTO
     * @param orderItemId
     * @return
     */
    public OtherDTO getOrderDTOForOther(Long orderItemId) {
        OtherDTO otherOrder=new OtherDTO();
        OrderItem orderItem=this.getOrderItemById(orderItemId);
        if (orderItem!=null){
            otherOrder.setSkuId(orderItem.getSkuId());
            otherOrder.setSkuName(orderItem.getName());
            Long orderId=orderItem.getOrderId();
            Order order=orderDao.getOrderById(orderId).getData();
            if (order!=null){
                otherOrder.setShopId(order.getShop().getShopId());
                otherOrder.setOrderSn(order.getOrderSn());
                return otherOrder;
            }
        }
        return null;
    }

    private COrderItem translate(OrderItem orderItem)
    {
        //和core中类型不一致，转化一下
        COrderItem cOrderItem=new COrderItem();
        cOrderItem.setSkuId(orderItem.getSkuId());
        cOrderItem.setBeShareId(orderItem.getBeShareId());
        cOrderItem.setCouponActivityId(orderItem.getCouponActivityId());
        cOrderItem.setDiscount(orderItem.getDiscount());
        cOrderItem.setId(orderItem.getId());
        cOrderItem.setName(orderItem.getName());
        cOrderItem.setOrderId(orderItem.getOrderId());
        cOrderItem.setPrice(orderItem.getPrice());
        cOrderItem.setQuantity(orderItem.getQuantity());
        return cOrderItem;
    }

    private OrderItem translateFor(COrderItem orderItem)
    {
        //和core中类型不一致，转化一下
        OrderItem cOrderItem=new OrderItem();
        cOrderItem.setSkuId(orderItem.getSkuId());
        cOrderItem.setBeShareId(orderItem.getBeShareId());
        cOrderItem.setCouponActivityId(orderItem.getCouponActivityId());
        cOrderItem.setDiscount(orderItem.getDiscount());
        cOrderItem.setId(orderItem.getId());
        cOrderItem.setName(orderItem.getName());
        cOrderItem.setOrderId(orderItem.getOrderId());
        cOrderItem.setPrice(orderItem.getPrice());
        cOrderItem.setQuantity(orderItem.getQuantity());
        return cOrderItem;
    }

    public List<OrderItem> calculateDiscount(List<OrderItem> orderItems) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        List<COrderItem> orderItemList=new ArrayList<>(orderItems.size());

        Map<Long,List<COrderItem>> listMap=new HashMap<>(orderItems.size());
        for (OrderItem orderItem :orderItems) {
            COrderItem cOrderItem=this.translate(orderItem);
            listMap.computeIfAbsent(orderItem.getCouponActivityId(), k->new ArrayList<>()).add(cOrderItem);
        }
        for (Map.Entry<Long, List<COrderItem>> entry : listMap.entrySet()) {
            Long k = entry.getKey();
            List<COrderItem> v = entry.getValue();
            String itemJson = JacksonUtil.toJson(v);
            BaseCouponDiscount baseCouponDiscount = BaseCouponDiscount.getInstance(itemJson);
            orderItemList.addAll(baseCouponDiscount.compute(v));
        }
        List<OrderItem> items=new ArrayList<>(orderItemList.size());
        orderItemList.forEach(v->{
            OrderItem orderItem=this.translateFor(v);
            items.add(orderItem);
        });
        return  items;
    }
}
