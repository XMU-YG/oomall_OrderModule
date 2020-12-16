package cn.edu.xmu.order.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.RandomCaptcha;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.order.mapper.OrderMapper;
import cn.edu.xmu.order.mapper.OrderPoMapper;
import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.bo.SimpleOrder;
import cn.edu.xmu.order.model.bo.SimpleOrderItem;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderItemPoExample;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.po.OrderPoExample;
import cn.edu.xmu.order.model.vo.AddressVo;

import cn.edu.xmu.order.util.OrderStatus;
import cn.edu.xmu.order.util.OrderType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Repository

public class OrderDao {

    private  static  final Logger logger = LoggerFactory.getLogger(OrderDao.class);
    @Autowired
    private OrderPoMapper orderPoMapper;
    @Autowired
    private OrderItemPoMapper orderItemPoMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Value("${orderservice.stock.expiretime}")
    private long timeout;

    /**
     * 分页查询顾客所有订单概要信息
     * @param customerId
     * @param orderSn
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return 订单概要视图
     * @author Gang Ye
     * @created 2020/11/26
     * @modified 2020/12/8 by Gang Ye 解决String转换LocalDate异常
     */
    public ReturnObject<PageInfo<VoObject>> getAllSimpleOrders(Long customerId,String orderSn, Integer state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize){

        //System.out.println("DAO:"+customerId+" "+orderSn+" "+state+" "+beginTime+" "+endTime+" "+page+" "+pageSize);
        //设置查询条件
        OrderPoExample example=new OrderPoExample();
        OrderPoExample.Criteria criteria=example.createCriteria();
        criteria.andCustomerIdEqualTo(customerId);
        criteria.andBeDeletedEqualTo((byte) 0);//未逻辑删除
        if (orderSn!=null){
            criteria.andOrderSnEqualTo(orderSn);
        }
        if (state!=null){
            criteria.andStateEqualTo(state.byteValue());
        }
        if (beginTime!=null){
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
        }
        if (endTime!=null){
            criteria.andGmtCreateLessThanOrEqualTo(endTime);
        }
        List<OrderPo> orderPos=null;
        try{
            orderPos=orderPoMapper.selectByExample(example);

        }catch (DataAccessException e){
            logger.error("getAllSimpleOrders:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        List<VoObject> ret=new ArrayList<>(orderPos.size());
        for(OrderPo po:orderPos){
            SimpleOrder simpleOrder=new SimpleOrder(po);
            logger.debug("getAllSimpleOrders: orderSn: "+po.getOrderSn()+"   state:  "+po.getState());
            ret.add(simpleOrder);
        }
        /**
         * ret 分页内容，其内容为bo（实现VoObject接口）对象
         * simpleOrderPoPage可以看做分页的大容器，由po构造
         * simpleOrderPage 是返回的分页对象，由ret构造，大小由simpleOrderPoPage确定
         */
        PageHelper.startPage(page,pageSize);
        PageInfo<OrderPo> simpleOrderPoPage=PageInfo.of(orderPos);
        PageInfo<VoObject> simpleOrderPage=new PageInfo<>(ret);
        simpleOrderPage.setPages(simpleOrderPoPage.getPages());
        simpleOrderPage.setPageNum(simpleOrderPoPage.getPageNum());
        simpleOrderPage.setPageSize(simpleOrderPoPage.getPageSize());
        simpleOrderPage.setTotal(simpleOrderPoPage.getTotal());
        return new ReturnObject<>(simpleOrderPage);
    }

    /**
     * 根据订单号查询订单
     * @param orderId 订单id
     * @return 订单详细信息Order
     * @author Gang Ye
     * @created 2020/11/27
     * @modified 2020/12/15 Gang Ye 代码重构
     *
     */
    public ReturnObject<Order> getOrderById(Long orderId){
        ReturnObject<Order> orderReturnObject=null;

        OrderPo orderPo=null;
        try{
            orderPo=orderPoMapper.selectByPrimaryKey(orderId);
        }catch (DataAccessException e){
            logger.error("getOrderById:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (orderPo==null||orderPo.getBeDeleted()== 1){
            logger.debug("getOrderById error: it's empty!  orderId:  "+orderId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单号不存在");

        }
        logger.debug("getOrderById success！  orderId:  "+orderId);
        Order order=new Order(orderPo);
        orderReturnObject=new ReturnObject<>(order);
        return orderReturnObject;
    }

    /**
     * 买家修改本人未发货订单地址信息
     * @param customerId
     * @param orderId
     * @param vo
     * @return
     * @author Gang Ye
     * @created 2020/11/29
     */
    public ReturnObject modifySelfOrderAddressById(Long customerId, Long orderId, AddressVo vo){
        ReturnObject orderReturnObject=null;
        OrderPo orderPo=null;
        try{
            orderPo=orderPoMapper.selectByPrimaryKey(orderId);
        }catch (DataAccessException e){
            logger.error("modifySelfOrderAddressById:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (orderPo==null||orderPo.getBeDeleted()==1){
            logger.debug("customer modifySelfOrderAddressById error: it's empty!  orderId:  "+orderId+"   customerId:  "+customerId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"订单号不存在");
        }
        else if(orderPo.getCustomerId().equals(customerId)){
            if (orderPo.getState()!=OrderStatus.CANCELED.getCode()
                    &&orderPo.getState()!=OrderStatus.FINISHED.getCode()
                    &&(orderPo.getSubstate()==null||orderPo.getSubstate()!=OrderStatus.SHIPPED.getCode())){  //未发货前都可以修改
                logger.debug("customer modifySelfOrderAddressById success！  orderId:  "+orderId+"   customerId:  "+customerId);
                orderPo.setRegionId(vo.getRegionId());
                orderPo.setAddress(vo.getAddress());
                orderPo.setConsignee(vo.getConsignee());
                orderPo.setMobile(vo.getMobile());
                orderPo.setGmtModified(LocalDateTime.now());
                orderPoMapper.updateByPrimaryKey(orderPo);
                orderReturnObject=new ReturnObject(ResponseCode.OK,"订单地址修改成功");
                return orderReturnObject;
            }
            else {
                logger.debug("customer modifySelfOrderAddressById error！the order state: "+orderPo.getSubstate()+" ("+orderPo.getState()+" )");
                return new ReturnObject(ResponseCode.ORDER_STATENOTALLOW,"订单已发货");
            }

        }
        else{
            logger.debug("customer modifySelfOrderAddressById error: don't have privilege!   orderId:  "+orderId+"   customerId:  "+customerId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权修改，不是自己订单");
        }

    }

    /**
     * 买家取消本人已发货前订单，数据库中逻辑删除
     * @param customerId
     * @param orderId
     * @return
     * @author Gang Ye
     * @created 2020/11/30
     * @modified Gang Ye 修改可取消的状态为待付款或待收货
     */
    public ReturnObject deleteOrderByCus(Long customerId, Long orderId) {
        ReturnObject orderReturnObject=null;
        OrderPo orderPo=null;
        try{
            orderPo=orderPoMapper.selectByPrimaryKey(orderId);
        }catch (DataAccessException e){
            logger.error("deleteSelfOrderById:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (orderPo==null){
            logger.debug("customer deleteSelfOrderById error: it's empty!  orderId:  "+orderId+"   customerId:  "+customerId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"订单号不存在");

        }
        else if(orderPo.getCustomerId().equals(customerId)){

            //取消
            if (orderPo.getState()==OrderStatus.WAIT_FOR_PAID.getCode()
                    ||orderPo.getState()==OrderStatus.WAIT_FOR_RECEIVE.getCode()){  //待付款 待收货
                logger.debug("customer cancelSelfOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId+" state: "+orderPo.getState());
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setState((byte)OrderStatus.CANCELED.getCode());//订单取消
                orderPoMapper.updateByPrimaryKey(orderPo);
                orderReturnObject=new ReturnObject(ResponseCode.OK,"已取消");
                return orderReturnObject;
            }
            //逻辑删除
            else if (orderPo.getState()==OrderStatus.FINISHED.getCode()){ //已完成
                logger.debug("customer deleteSelfOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId+" state: "+orderPo.getState());
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setBeDeleted((byte) 1);//1为订单逻辑删除
                orderPoMapper.updateByPrimaryKey(orderPo);
                orderReturnObject=new ReturnObject(ResponseCode.OK,"订单删除成功");
                return orderReturnObject;
            }
            else {
                logger.debug("customer deleteSelfOrderById error！the order state: "+orderPo.getState());
                return new ReturnObject(ResponseCode.ORDER_STATENOTALLOW);
            }

        }
        else{
            logger.debug("customer deleteSelfOrderById error: don't have privilege!   orderId:  "+orderId+"   customerId:  "+customerId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
    }

    /**
     * 买家标记确认收货
     * @param customerId
     * @param orderId
     * @return
     * @author Gang Ye
     * @created 2020/11/30
     */
    public ReturnObject confirmSelfOrderById(Long customerId, Long orderId) {
        ReturnObject orderReturnObject=null;
        OrderPo orderPo=null;
        try{
            orderPo=orderPoMapper.selectByPrimaryKey(orderId);
        }catch (DataAccessException e){
            logger.error("confirmSelfOrderById:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (orderPo==null){
            logger.debug("customer confirmSelfOrderById error: it's empty!  orderId:  "+orderId+"   customerId:  "+customerId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"订单号不存在");
        }
        else if(orderPo.getCustomerId().equals(customerId)){
            if (orderPo.getState()==OrderStatus.WAIT_FOR_RECEIVE.getCode()){  //为待收货
                logger.debug("customer confirmSelfOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId);
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setConfirmTime(LocalDateTime.now());
                orderPo.setState((byte)OrderStatus.FINISHED.getCode());//已收货
                orderPo.setSubstate(null);
                orderPoMapper.updateByPrimaryKey(orderPo);
                orderReturnObject=new ReturnObject(ResponseCode.OK,"订单确认收货成功");
                return orderReturnObject;
            }
            else {
                logger.debug("customer confirmSelfOrderById error！the order state: "+orderPo.getState());
                return new ReturnObject(ResponseCode.ORDER_STATENOTALLOW,"订单状态禁止");
            }
        }
        else{
            logger.debug("customer deleteSelfOrderById error: don't have privilege!   orderId:  "+orderId+"   customerId:  "+customerId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权取消，不是自己订单");
        }
    }

    /**
     * 将团购订单转为预售订单
     * @param customerId
     * @param id
     * @return
     */
    public ReturnObject transLateGroToNor(Long customerId, Long id) {
        OrderPo orderPo=null;
        try{
            OrderPoExample example=new OrderPoExample();
            OrderPoExample.Criteria criteria=example.createCriteria();
            criteria.andIdEqualTo(id);
            criteria.andCustomerIdEqualTo(customerId);
            List<OrderPo> orderPos= orderPoMapper.selectByExample(example);
            orderPo=orderPos.isEmpty()?null:orderPos.get(0);
        }catch (DataAccessException e){
            logger.error("transLateGroToNor:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (orderPo==null){
            logger.debug("customer transLateGroToNor error: it's empty!  orderId:  "+id+"   customerId:  "+customerId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"订单号不存在");
        }
        else{
            //未成团
            if (orderPo.getSubstate()!=null&&orderPo.getSubstate()==OrderStatus.UNGROUP.getCode()){
                orderPo.setSubstate(null);
                orderPo.setOrderType(OrderType.NORMAL.getCode());
                orderPoMapper.updateByPrimaryKey(orderPo);
                return new ReturnObject(ResponseCode.OK);
            }
            else{
                logger.debug("customer transLateGroToNor error！the order state: "+orderPo.getState());
                return new ReturnObject(ResponseCode.ORDER_STATENOTALLOW,"订单状态禁止");
            }
        }
    }

    /**
     * 卖家查询订单概要
     * @param shopId
     * @param customerId
     * @param orderSn
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<VoObject>> getShopSelfSimpleOrders(Long shopId, Long customerId, String orderSn, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        //设置查询条件
        OrderPoExample example=new OrderPoExample();
        OrderPoExample.Criteria criteria=example.createCriteria();
        if (customerId!=null){
            criteria.andCustomerIdEqualTo(customerId);
        }
        if (orderSn!=null){
            criteria.andOrderSnEqualTo(orderSn);
        }
        if (beginTime!=null){
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
        }
        if (endTime!=null){
            criteria.andGmtCreateLessThanOrEqualTo(endTime);
        }
        if (shopId!=0){
            criteria.andShopIdEqualTo(shopId);
        }
        List<OrderPo> orderPos=null;
        try{
            orderPos=orderPoMapper.selectByExample(example);
        }catch (DataAccessException e){
            logger.error("getShopSelfSimpleOrders:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        logger.debug("getShopSelfSimpleOrders: shopId: "+shopId);
        List<VoObject> ret=new ArrayList<>(orderPos.size());
        for(OrderPo po:orderPos){
            SimpleOrder simpleOrder=new SimpleOrder(po);
            ret.add(simpleOrder);
        }

        /**
         * ret 分页内容，其内容为bo（实现VoObject接口）对象
         * simpleOrderPoPage可以看做分页的大容器，由po构造
         * simpleOrderPage 是返回的分页对象，由ret构造，大小由simpleOrderPoPage确定
         */
        PageHelper.startPage(page,pageSize);
        PageInfo<OrderPo> simpleOrderPoPage=PageInfo.of(orderPos);
        PageInfo<VoObject> simpleOrderPage=new PageInfo<>(ret);
        simpleOrderPage.setPages(simpleOrderPoPage.getPages());
        simpleOrderPage.setPageNum(simpleOrderPoPage.getPageNum());
        simpleOrderPage.setPageSize(simpleOrderPoPage.getPageSize());
        simpleOrderPage.setTotal(simpleOrderPoPage.getTotal());
        return new ReturnObject<>(simpleOrderPage);
    }

    /**
     * 卖家修改留言
     * @param shopId
     * @param orderId
     * @param message
     * @return
     * @Gang Ye
     * @modified Gang Ye 增加shop=0判断
     */
    public ReturnObject modifyOrderMessage(Long shopId, Long orderId, String message) {
        ReturnObject orderReturnObject=null;
        OrderPo orderPo=null;
        try{
            orderPo=orderPoMapper.selectByPrimaryKey(orderId);
        }catch (DataAccessException e){
            logger.error("modifyOrderMessage:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (orderPo==null){
            logger.debug("modifyOrderMessage error: it's empty!  orderId:  "+orderId+"   customerId:  "+shopId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"订单号不存在");

        }
        else{
            if (shopId!=0L){
                if (orderPo.getShopId()==null||!orderPo.getShopId().equals(shopId)){
                    logger.debug("modifyOrderMessage error: don't have privilege!   orderId:  "+orderId+"   shopId:  "+shopId);
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权修改，不是自己订单");
                }
            }
            logger.debug("modifyOrderMessage success！  orderId:  "+orderId);
            orderPo.setGmtModified(LocalDateTime.now());
            orderPo.setMessage(message);
            orderPoMapper.updateByPrimaryKey(orderPo);
            orderReturnObject=new ReturnObject(ResponseCode.OK,"订单留言成功");
            return orderReturnObject;
        }
    }

    /**
     * 卖家取消，删除订单
     * @param shopId
     * @param orderId
     * @return
     */
    public ReturnObject deleteOrderByShop(Long shopId, Long orderId) {
        ReturnObject orderReturnObject=null;
        OrderPo orderPo=null;
        try{
            orderPo=orderPoMapper.selectByPrimaryKey(orderId);
        }catch (DataAccessException e){
            logger.error("deleteShopOrderById:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (orderPo==null){
            logger.debug("deleteShopOrder error: it's empty!  orderId:  "+orderId+"   shopId:  "+shopId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"订单号不存在");
        }
        else{
            if (shopId!=0){
                if (orderPo.getShopId()==null||!orderPo.getShopId().equals(shopId)){
                    logger.debug("deleteShopOrder error: don't have privilege!   orderId:  "+orderId+"   shopId:  "+shopId);
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权取消，不是自己订单");
                }
            }
            if (orderPo.getState()==OrderStatus.WAIT_FOR_PAID.getCode()
                    ||orderPo.getState()==OrderStatus.WAIT_FOR_RECEIVE.getCode()){  //待收货 待付款都可以取消
                logger.debug("deleteShopOrder success！  orderId:  "+orderId+"   shopId:  "+shopId);
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setBeDeleted((byte) 1);
                orderPo.setState((byte) OrderStatus.CANCELED.getCode());//0为订单取消
                orderPoMapper.updateByPrimaryKey(orderPo);
                orderReturnObject=new ReturnObject(ResponseCode.OK,"订单取消成功");
                return orderReturnObject;
            } else {
                logger.debug("deleteShopOrder error！the order state: "+orderPo.getState());
                return new ReturnObject(ResponseCode.ORDER_STATENOTALLOW);
            }
        }
    }

    /**
     * 卖家标记发货
     * @param shopId
     * @param orderId
     * @param freightSn
     * @return
     */
    public ReturnObject deliverOrderByShop(Long shopId, Long orderId, String freightSn) {
        ReturnObject orderReturnObject=null;
        OrderPo orderPo=null;
        try{
            orderPo=orderPoMapper.selectByPrimaryKey(orderId);
        }catch (DataAccessException e){
            logger.error("deliverShopOrder:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (orderPo==null){
            logger.debug("deliverShopOrder error: it's empty!  orderId:  "+orderId+"   shopId:  "+shopId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"订单号不存在");

        }
        else{
            if (shopId!=0L){
                if (orderPo.getShopId()==null||!orderPo.getShopId().equals(shopId)){
                    logger.debug("deliverShopOrder error: don't have privilege!   orderId:  "+orderId+"   shopId:  "+shopId);
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权修改，不是自己订单");
                }
            }
            if (orderPo.getSubstate()==OrderStatus.PAID_SUCCEED.getCode()){  //待发货
                logger.debug("deliverShopOrder success！  orderId:  "+orderId+"   shopId:  "+shopId);
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setShipmentSn(freightSn);
                orderPo.setSubstate((byte)OrderStatus.WAIT_FOR_RECEIVE.getCode());//已发货
                orderPoMapper.updateByPrimaryKeySelective(orderPo);
                orderReturnObject=new ReturnObject(ResponseCode.OK,"订单发货成功");
                return orderReturnObject;
            }
            else {
                logger.debug("deliverShopOrder error！the order state: "+orderPo.getState());
                return new ReturnObject(ResponseCode.ORDER_STATENOTALLOW,"订单状态禁止");
            }

        }
    }

    /**
     * 秒杀扣库存
     * @param skuId
     * @param quantity
     * @return
     */
    public int deductStock(Long skuId, Integer quantity) {
        String key="sku_lock_"+skuId;
        String value= RandomCaptcha.getRandomString(11);

        try{
            boolean flag=redisTemplate.opsForValue().setIfAbsent(key,value,Common.addRandomTime(timeout),TimeUnit.SECONDS);
            if (flag){
                logger.debug("redis lock successful!  key:  "+key);
                String goodsKey="sku:"+skuId;
                Integer stock=(Integer) redisTemplate.opsForHash().get(goodsKey,"quantity");
                //stock=10;
                if (stock>quantity){
                    redisTemplate.opsForHash().increment(goodsKey,"quantity",-quantity);
                    String lockValue= (String) redisTemplate.opsForValue().get(key);
                    if (lockValue.equals(value)){
                        redisTemplate.delete(key);
                        logger.debug("redis unlock successful! key:  "+key);
                    }
                    return 1;
                }

                String lockValue= (String) redisTemplate.opsForValue().get(key);
                assert lockValue != null;
                if (lockValue.equals(value)){
                    redisTemplate.delete(key);
                    logger.debug("redis unlock! key:  "+key);
                }
            }

            return 0;
        }catch (NullPointerException e){
            logger.error("redis error!"+e.getMessage());
            return 0;
        }

    }

    /**
     * 插入order
     * @param orderPo
     * @return
     */
    public ReturnObject<Long> insertOrder(OrderPo orderPo) {
        ReturnObject<Long> returnObject=null;
        try{
            int ret=orderPoMapper.insert(orderPo);
            if (ret==1){
                logger.debug("insertOrder:  orderId:"+orderPo.getId()+"    customerId: "+orderPo.getCustomerId());
                returnObject=new ReturnObject<Long>(orderPo.getId());
                return returnObject;
            }
            else{
                logger.debug("insertOrder error:  orderId:"+orderPo.getId()+"    customerId: "+orderPo.getCustomerId());
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
            }

        }catch (DataAccessException e){
            logger.error("insertOrder:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 获得订单状态
     * @param orderId
     * @return
     */
    public Byte getOrderState(Long orderId) {
        return orderMapper.getStateById(orderId);
    }

    /**
     * 获得所有订单
     * @return
     */
    public List<SimpleOrder> getAllOrders() {
        List<OrderPo> orderPos=orderMapper.getAllOrders();
        List<SimpleOrder> simpleOrders=new ArrayList<>(orderPos.size());
        for (OrderPo o : orderPos) {
            if (o.getBeDeleted() != 1&&o.getGmtCreate().plusDays(7).getDayOfMonth()==LocalDateTime.now().getDayOfMonth()){
                SimpleOrder simpleOrder=new SimpleOrder(o);
                simpleOrders.add(simpleOrder);
            }
        }
        return simpleOrders;
    }
}
