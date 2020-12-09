package cn.edu.xmu.order.service;

import cn.edu.xmu.ooad.model.VoObject;
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
import cn.edu.xmu.order.service.impl.DeductStockImpl;
import cn.edu.xmu.order.service.impl.GoodsServiceImpl;
import cn.edu.xmu.order.service.impl.OtherServiceImpl;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单模块服务层
 * @author Gang Ye
 */
@Service
public class OrderService implements DeductStockImpl{
    private Logger logger=LoggerFactory.getLogger(OrderService.class);
    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private OrderDao orderDao;

    @Transactional
    public ReturnObject<PageInfo<VoObject>> getAllSimpleOrders(Long customerId, String orderSn, Integer state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize){
        return orderDao.getAllSimpleOrders(customerId,orderSn,state,beginTime,endTime,page,pageSize);
    }


    @Transactional
    public ReturnObject<VoObject> getOrderById(Long customerId,Long orderId){
        ReturnObject<VoObject> returnObject= orderDao.getOrderById(customerId,orderId);
        Order order=(Order)returnObject.getData();

        Customer customer=new Customer();
        customer.setCustomerId(order.getCustomer().getCustomerId());

        Shop shop=new Shop();
        shop.setShopId(order.getShop().getShopId());

        order.setCustomer(customer);
        order.setShop(shop);

        return new ReturnObject<>(order);
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
        Order order=(Order)returnObject.getData();

        Customer customer=new Customer();
        customer.setCustomerId(order.getCustomer().getCustomerId());

        Shop shop=new Shop();
        shop.setShopId(order.getShop().getShopId());

        order.setCustomer(customer);
        order.setShop(shop);

        return new ReturnObject<>(order);
    }

    @Transactional
    public ReturnObject modifyOrderMessage(Long shopId, Long orderId, String message) {
        return orderDao.modifyOrderMessage(shopId,orderId,message);
    }

    @Transactional
    public ReturnObject deleteShopOrder(Long shopId, Long orderId) {
        return orderDao.deleteShopOrder(shopId,orderId);
    }

    @Transactional
    public ReturnObject deliverShopOrder(Long shopId, Long orderId, String freightSn) {
        return orderDao.deliverShopOrder(shopId,orderId,freightSn);
    }

    @Transactional
    public ReturnObject<Long> insertOrder(OrderPo orderPo){
        return orderDao.insertOrder(orderPo);
    }
    public boolean verifyOrderByCustomerId(Long customerId,Long orderId) {

        ReturnObject<VoObject> returnObject=orderDao.getOrderById(customerId,orderId);
        if (returnObject.getCode().equals(ResponseCode.OK)){
            return true;
        }
        return false;
    }

    public boolean verifyOrderByShopId(Long shopId,Long orderId) {
        ReturnObject<VoObject> returnObject=orderDao.getShopSelfOrder(shopId,orderId);
        if (returnObject.getCode().equals(ResponseCode.OK)){
            return true;
        }
        return false;
    }

    public ReturnObject<List<OrderItemPo>> disposeNorOrderItemsPo(List<OrderItemPo> orderItemPos){
        ArrayList<OrderItemPo> order_goodsList=new ArrayList<>(orderItemPos.size());
        GoodsServiceImpl goodsService=null;
        OtherServiceImpl otherService=null;
        //检查库存
        for (OrderItemPo orderItemPo:orderItemPos) {
            OrderItemPo order_goods=goodsService.findGoodsBySkuId(orderItemPo.getGoodsSkuId());
            if (orderItemPo.getQuantity()>order_goods.getQuantity()){
                //库存不足
                return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
            }
            order_goods.setQuantity(orderItemPo.getQuantity());
            order_goods.setCouponActivityId(orderItemPo.getCouponActivityId());
            order_goods.setBeShareId(otherService.getBeSharedIdBySkuId(orderItemPo.getGoodsSkuId()));

            order_goodsList.add(order_goods);
        }
        //扣库存
        DeductStockImpl goodsDeduct=null;
        for (OrderItemPo order_goods:order_goodsList) {
            if (!goodsDeduct.deductStock(order_goods.getGoodsSkuId(),order_goods.getQuantity())) {
                //库存不足
                return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
            }
        }
        return new ReturnObject<>(order_goodsList);
    }

    public Long calculateDiscount(){
        return (long)100;
    }

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
        for (OrderItemPo o:orderGoods
             ) {
            sum+=o.getPrice()*o.getQuantity();
        }
        return sum;
    }


    /**
     * 秒杀扣库存
     * @param skuId
     * @param quantity
     * @return
     */
    @Transactional
    @Override
    public boolean deductStock(Long skuId, Integer quantity) {

        int flag=orderDao.deductStock(skuId,quantity);
        switch (flag){
            case 1:
                return true;
            case 0:{
            }
            case -1:
                return false;
        }
        return false;
    }

    public ReturnObject insertOrderItem(OrderItemPo orderItemPo) {
        return orderDao.insertOrderItem(orderItemPo);
    }

    public ReturnObject<List> getOrderAllStates(Long customerId) {
        return orderDao.getOrderAllStates(customerId);
    }


//    void classifyOrders(){
//        Map<Long,List<OrderGoods>> goodsMapByShop=new HashMap<>(goods.size());
//        for (OrderGoods g :goods) {
//            goodsMapByShop.computeIfAbsent(g.getShopId(),k->new ArrayList<OrderGoods>()).add(g);
//        }
//
//        ArrayList<OrderPo> orderPos=new ArrayList<>(goodsMapByShop.size());
//        //分单
//        goodsMapByShop.forEach((k,v)->{
//            //子订单
//            OrderPo po=new OrderPo();
//            po.setOrderSn(Common.genSeqNum());
//            po.setShopId(k);
//            po.setCustomerId(customerId);
//            OrderServiceImpl freightService=null;
//            po.setFreightPrice(freightService.calculateFreight(v));
//            OrderServiceImpl otherService=null;
//            po.setDiscountPrice(this.calculateDiscount());
//            po.setRebateNum(otherService.calculateRebateNum(v,customerId));
//
//        });
//    }

}
