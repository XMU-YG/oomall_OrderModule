package cn.edu.xmu.order.service;

import cn.edu.xmu.goodsprovider.Module.ShopRetVo;
import cn.edu.xmu.goodsprovider.flashsale.FlashService;
import cn.edu.xmu.goodsprovider.goods.GoodsInner;
import cn.edu.xmu.goodsprovider.goods.ShopService;
import cn.edu.xmu.ooad.model.VoObject;

import cn.edu.xmu.ooad.order.bo.COrderItem;
import cn.edu.xmu.ooad.order.discount.BaseCouponDiscount;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.JacksonUtil;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.dao.OrderDao;
import cn.edu.xmu.order.dao.OrderItemDao;

import cn.edu.xmu.order.util.CreateOrderService;
import cn.edu.xmu.goodsprovider.Module.OrderGoods;
import cn.edu.xmu.order_provider.model.order.GoodsDTO;
import cn.edu.xmu.order_provider.model.order.OtherDTO;
import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.vo.AddressVo;

import cn.edu.xmu.share.dubbo.ShareService;
import cn.edu.xmu.user.dubbo.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import cn.edu.xmu.order.model.bo.*;
import cn.edu.xmu.order.model.vo.OrderItemVo;
import cn.edu.xmu.order.model.vo.OrderVo;
import cn.edu.xmu.order.util.OrderStatus;

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

    @DubboReference(version = "0.0.1", check=false)
    private ShopService shopService;

    @DubboReference(version = "0.0.1", check=false)
    private GoodsInner goodsInner;

    @DubboReference(version = "0.0.1", check=false)
    private FlashService flashService;

    @DubboReference(version = "0.0.1", check=false)
    private ShareService shareService;

    @DubboReference(version = "0.0.1", check=false)
    private UserService otherService;

    /**
     * 根据订单号查询订单明细
     * @param orderId
     * @return
     */
    public List<OrderItem> findOrderItemsByOrderId(Long orderId){
        return orderItemDao.getOrderItemsByOrderId(orderId);
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
            if(order.getCustomer().getId().equals(customerId)){

                //构造完整Order详情
                List<SimpleOrderItem> orderItems=orderItemDao.getSimOrderItemsByOrderId(order.getId());
                order.setSimpleOrderItemList(orderItems);
                Long shopId=1L;
                Customer customer=JacksonUtil.toObj(otherService.findCustomerById(customerId),Customer.class);
                ShopRetVo shopRetVo=JacksonUtil.toObj(shopService.getShopById(shopId),ShopRetVo.class);
                assert shopRetVo != null;
                Shop shop=new Shop(shopRetVo);
                order.setCustomer(customer);
                order.setShop(shop);
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
        ReturnObject check=verifyOrderByCustomerId(customerId,orderId);
        if (check.getCode().equals(ResponseCode.OK)){
            //是本人的
            ReturnObject returnObject=orderDao.deleteOrderById(orderId);
            //加库存
//            if (returnObject.getErrmsg().equals("已取消")){
//                List<SimpleOrderItem> simpleOrderItems=orderItemDao.getSimOrderItemsByOrderId(orderId);
//                for (SimpleOrderItem item:simpleOrderItems) {
//                    goodsInner.deductNorStock(item.getSkuId(),item.getQuantity());
//                }
//            }
            return returnObject;
        }
        return check;
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
        if (object.getCode().equals(ResponseCode.OK)){
            Order order=(Order) object.getData();
            if(shopId==0L||order.getShop().getId().equals(shopId)){
                logger.debug("shop getOrder success！  orderId:  "+id+"   shopId:  "+shopId);
                //构造完整Order详情
                List<SimpleOrderItem> orderItems=orderItemDao.getSimOrderItemsByOrderId(order.getId());
                order.setSimpleOrderItemList(orderItems);
                Long customerId=order.getCustomer().getId();
                Long sid=order.getShop().getId();
                Customer customer=JacksonUtil.toObj(otherService.findCustomerById(customerId),Customer.class);
                ShopRetVo shopRetVo=JacksonUtil.toObj(shopService.getShopById(sid),ShopRetVo.class);
                assert shopRetVo != null;
                Shop shop=new Shop(shopRetVo);
                order.setShop(shop);
                order.setCustomer(customer);
                logger.debug(JacksonUtil.toJson(order));
                return new ReturnObject<>(order);
            } else{
                logger.debug("shop getOrderById error: don't have privilege!   orderId:  "+id+"  shopId:  "+shopId);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权访问，不是自己订单");
            }
        } else{
            return object;
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
    public ReturnObject verifyOrderByCustomerId(Long customerId, Long orderId) {
        ReturnObject<Order> ret=orderDao.getOrderById(orderId);
        logger.debug("verify...  ");
        if (ret.getCode().equals(ResponseCode.OK)){
            if (ret.getData().getCustomer().getId().equals(customerId)){
                logger.debug("verify true!");
                return new ReturnObject(ResponseCode.OK);
            }else {
                logger.debug("verify error!");
                return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }
        return new ReturnObject(ret.getCode());
    }

    /**
     * 判断订单是否属于该店铺
     * @param shopId
     * @param orderId
     * @return
     */
    public ReturnObject verifyOrderByShopId(Long shopId,Long orderId) {
        ReturnObject<Order> ret=orderDao.getOrderById(orderId);
        if (ret.getCode().equals(ResponseCode.OK)){
            if (ret.getData().getShop().getId().equals(shopId)){
                logger.debug("verify true!");
                return new ReturnObject(ResponseCode.OK);
            }else {
                logger.debug("verify error!");
                return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }
        return new ReturnObject(ret.getCode());
    }

    /**
     * 处理普通商品订单明细
     * 扣库存(普通商品)
     * @param goodsList 商品列表
     * @return 是否成功
     * @modified by Gang Ye  修改业务只有扣库存
     */
    @Transactional
    public ReturnObject disposeNorGoodsList(Long actId,List<OrderGoods> goodsList, String beanName)  {

        CreateOrderService deductService=applicationContext.getBean(beanName, CreateOrderService.class);
        //扣库存
        //存储以扣库存的商品skuId和quantity，便于回滚,局域变量，线程安全
        Map<Long,Integer> map=new HashMap<>(goodsList.size());
        for (OrderGoods po:goodsList) {
            //扣库存,不同类型的商品扣普通的库存（普通，团购，预售）
            boolean deductSuccessful=deductService.deductStock(actId,po.getGoods_sku_id(),po.getQuantity());
            if (!deductSuccessful) {
                //库存不足
                //库存回滚
                map.forEach((k,v)->{
                    deductService.deductStock(actId,k,-v);
                });
                return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
            }
            //扣成功则存入map
            map.put(po.getGoods_sku_id(),po.getQuantity());
            System.out.println(po.getGoods_sku_id()+"  "+po.getQuantity()+"   成功");
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
     * 计算优惠总数额
     * @return
     */
    public Long totalDiscount(List<OrderItem> orderItems){
        Long sum=0L;
        for (OrderItem po:orderItems) {
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
        logger.debug("计算总价："+sum);
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
     * @param vo 新订单信息视图
     * @return >0 orderId  =0 库存不足 -1数据库错误
     * @author Gang Ye
     */
    public Long createAfterSaleOrder(Long customerId,Long shopId, OrderVo vo) {

        OrderPo orderPo=vo.createOrderPo();
        orderPo.setGmtCreate(LocalDateTime.now());
        List<OrderItemVo> orderItemVos=vo.getOrderItems();
        List<OrderItemPo> orderItemPos=vo.createOrderItemsPo();

        String orderSn= Common.genSeqNum();
        orderPo.setOrderSn(orderSn);
        orderPo.setOrderType((byte) 0);
        orderPo.setCustomerId(customerId);
        //普通商品
        ArrayList<OrderGoods> norGoodsArrayList=new ArrayList<>();
        //检查库存
        for (OrderItemVo orderItemVo:orderItemVos) {
            OrderItemPo orderItemPo=new OrderItemPo();
            String orderGoodsJson=flashService.findGoodsBySkuId(orderItemVo.getSkuId());
            OrderGoods order_goods= JacksonUtil.toObj(orderGoodsJson,OrderGoods.class);
            if (order_goods==null||orderItemVo.getQuantity()>order_goods.getQuantity()){
                //库存不足
                return 0L;
            }
            //构造OrderItemPo
            orderItemPo.setQuantity(orderItemVo.getQuantity());
            orderItemPo.setName(order_goods.getName());
            orderItemPo.setPrice(order_goods.getPrice());
            orderItemPo.setGoodsSkuId(order_goods.getGoods_sku_id());
            orderItemPo.setBeShareId(shareService.fillOrderItemByBeShare(orderItemPo.getGoodsSkuId(),customerId));
            orderItemPo.setGmtCreate(LocalDateTime.now());
            orderItemPo.setGmtModified(LocalDateTime.now());
            orderItemPos.add(orderItemPo);

            //商品数量属性设为购买数量，方便之后处理
            order_goods.setQuantity(orderItemVo.getQuantity());
            norGoodsArrayList.add(order_goods);
        }
        //处理购买的普通商品：扣库存
        ReturnObject nor=this.disposeNorGoodsList(0L,norGoodsArrayList,"NorOrderService");
        if (!nor.getCode().equals(ResponseCode.OK)){
            //库存不足
            return 0L;
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
                    return -1L;
                }
            }
            return orderId;
        }
        else {
            return -1L;
        }
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
            String goodsJson=flashService.findGoodsBySkuId(orderItem.getSkuId());
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
            po.setCustomerId(order.getCustomer().getId());
            po.setOrderType(order.getOrderType());
            po.setState((byte)OrderStatus.WAIT_FOR_RECEIVE.getCode());
            po.setOriginPrice(this.calculateOriginPrice(v));
            po.setFreightPrice(order.getFreightPrice()*(po.getOriginPrice()/order.getOriginPrice()));
            po.setDiscountPrice(this.totalDiscount(v));
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

    /**
     * 内部接口
     * 通过orderitemId获得orderItem对象
     * @param orderItemId
     * @return OrderItem
     */
    public OrderItem getOrderItemById(Long orderItemId) {
        return orderItemDao.getOrderItemById(orderItemId);
    }

    /**
     * 检查订单状态 未付款则取消，已付款则分单
     * @param orderId
     */
    public void checkOrderPayState(Long orderId){
        Byte state=orderDao.getOrderState(orderId);
        if (state==OrderStatus.WAIT_FOR_PAID.getCode()){
            logger.debug("order is canceled by system");
            orderDao.deleteOrderById(orderId);
        }
        else if (state==OrderStatus.WAIT_FOR_RECEIVE.getCode()){
            logger.debug("order has been classified");
            this.classifyOrder(orderId);
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
        logger.debug("dubbo service getOrderDTO for Other");
        OtherDTO otherOrder=new OtherDTO();
        OrderItem orderItem=this.getOrderItemById(orderItemId);
        if (orderItem!=null){
            otherOrder.setSkuId(orderItem.getSkuId());
            otherOrder.setSkuName(orderItem.getName());
            Long orderId=orderItem.getOrderId();
            otherOrder.setOrderId(orderId);
            Order order=orderDao.getOrderById(orderId).getData();
            if (order!=null){
                otherOrder.setShopId(order.getShop().getId());
                otherOrder.setOrderSn(order.getOrderSn());
                return otherOrder;
            }
        }
        return null;
    }

    private COrderItem translateForC(OrderItem orderItem) {
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

    private OrderItem translateForO(COrderItem orderItem) {
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

    /**
     * 计算优惠折扣
     * @param orderItems
     * @return
     * @throws JsonProcessingException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public List<OrderItem> calculateDiscount(List<OrderItem> orderItems) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        //存储转化后COrderItem
        List<COrderItem> orderItemList=new ArrayList<>(orderItems.size());
        Map<Long,List<COrderItem>> listMap=new HashMap<>(orderItems.size());
        //将OrderItem转化为COrderItem，并按couponActivity分类
        for (OrderItem orderItem :orderItems) {
            COrderItem cOrderItem=this.translateForC(orderItem);
            listMap.computeIfAbsent(cOrderItem.getCouponActivityId(), k->new ArrayList<>()).add(cOrderItem);
        }
        for (Map.Entry<Long, List<COrderItem>> entry : listMap.entrySet()) {
            Long k = entry.getKey();
            List<COrderItem> v = entry.getValue();
            String itemJson = JacksonUtil.toJson(v);
            BaseCouponDiscount baseCouponDiscount = BaseCouponDiscount.getInstance(itemJson);
            orderItemList.addAll(baseCouponDiscount.compute(v));
        }
        //COrderItem转化为OrderItem
        List<OrderItem> items=new ArrayList<>(orderItemList.size());
        orderItemList.forEach(v->{
            OrderItem orderItem=this.translateForO(v);
            items.add(orderItem);
        });
        return  items;
    }

    public GoodsDTO getGoodsDTOForGoods(Long orderItemId) {
        logger.debug("dubbo service getGoodsDTO for Goods");
        GoodsDTO goodsDTO=new GoodsDTO();
        OrderItem orderItem=this.getOrderItemById(orderItemId);
        if (orderItem!=null){
            goodsDTO.setSkuId(orderItem.getSkuId());
            Long orderId=orderItem.getOrderId();
            Order order=orderDao.getOrderById(orderId).getData();
            if (order!=null){
                goodsDTO.setCustomerId(order.getCustomer().getId());
                return goodsDTO;
            }
        }
        return null;
    }

    public boolean haveOrder(Long shopId) {
        return orderDao.haveOrder(shopId);
    }

    public Byte getOrderState(Long orderId) {
        logger.debug("dubbo service: getOrderState.");
        return orderDao.getOrderState(orderId);
    }

    public Long getOrderUser(Long orderId) {
        logger.debug("dubbo service: getOrderUser.");
        ReturnObject returnObject=orderDao.getOrderById(orderId);
        if (returnObject.getCode().equals(ResponseCode.OK)){
            Order order= (Order) returnObject.getData();
            if (order!=null){
                return order.getCustomer().getId();
            }
        }
        return 0L;
    }
}
