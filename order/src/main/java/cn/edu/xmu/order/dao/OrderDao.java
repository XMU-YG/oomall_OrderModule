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
import cn.edu.xmu.order.model.bo.OrderState;
import cn.edu.xmu.order.model.bo.SimpleOrder;
import cn.edu.xmu.order.model.bo.SimpleOrderItem;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderItemPoExample;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.po.OrderPoExample;
import cn.edu.xmu.order.model.vo.AddressVo;

import cn.edu.xmu.order.util.OrderStatus;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private int timeout=30;

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
     * 顾客查询自己名下订单详细信息
     * 缺少顾客信息查询，店铺信息查询
     * @param customerId 顾客id
     * @param orderId 订单id
     * @return 订单详细信息
     * @author Gang Ye
     * @created 2020/11/27
     * @modified 2020/11/30 by Gang Ye 增加orderItem插入
     *             2020/12/8 Gang Ye 将顾客与店铺信息设置移到Service层
     */
    public ReturnObject<VoObject> getOrderById(Long customerId,Long orderId){
        ReturnObject<VoObject> orderReturnObject=null;

        OrderPo orderPo=null;
        try{
            orderPo=orderPoMapper.selectByPrimaryKey(orderId);
        }catch (DataAccessException e){
            logger.error("getOrderById:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (orderPo==null||orderPo.getBeDeleted()== 1){
            logger.debug("customer getOrderById error: it's empty!  orderId:  "+orderId+"   customerId:  "+customerId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单号不存在");

        }
        else if(orderPo.getCustomerId().equals(customerId)){
            logger.debug("customer getOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId);
            Order order=new Order(orderPo);
            OrderItemPoExample itemPoExample=new OrderItemPoExample();
            OrderItemPoExample.Criteria criteria=itemPoExample.createCriteria();
            criteria.andOrderIdEqualTo(orderId);
            List<OrderItemPo> orderItemPos=orderItemPoMapper.selectByExample(itemPoExample);
            ArrayList<SimpleOrderItem> simpleOrderItems=new ArrayList<>(orderItemPos.size());
            for (OrderItemPo orderItemPo : orderItemPos){
                SimpleOrderItem simpleOrderItem=new SimpleOrderItem(orderItemPo);
                simpleOrderItems.add(simpleOrderItem);
            }

            order.setSimpleOrderItemList(simpleOrderItems);
            orderReturnObject=new ReturnObject<>(order);

            return orderReturnObject;
        }
        else{
            logger.debug("customer getOrderById error: don't have privilege!   orderId:  "+orderId+"   customerId:  "+customerId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权访问，不是自己订单");
        }
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
        if (orderPo==null){
            logger.debug("customer modifySelfOrderAddressById error: it's empty!  orderId:  "+orderId+"   customerId:  "+customerId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"订单号不存在");

        }
        else if(orderPo.getCustomerId().equals(customerId)){
            if (orderPo.getState().equals(15)){  //15为未发货
                logger.debug("customer modifySelfOrderAddressById success！  orderId:  "+orderId+"   customerId:  "+customerId);
                OrderPo po=vo.createPo();
                po.setGmtModified(LocalDateTime.now());
                po.setId(orderId);
                orderPoMapper.updateByPrimaryKeySelective(po);
                orderReturnObject=new ReturnObject(ResponseCode.OK,"订单地址修改成功");
                return orderReturnObject;
            }
            else {
                logger.debug("customer modifySelfOrderAddressById error！the order state: "+orderPo.getState());
                return new ReturnObject(ResponseCode.ORDER_STATENOTALLOW,"订单已发货");
            }

        }
        else{
            logger.debug("customer modifySelfOrderAddressById error: don't have privilege!   orderId:  "+orderId+"   customerId:  "+customerId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权修改，不是自己订单");
        }

    }

    /**
     * 买家取消本人未发货订单，数据库中逻辑删除
     * @param customerId
     * @param orderId
     * @return
     * @author Gang Ye
     * @created 2020/11/30
     */
    public ReturnObject deleteSelfOrderById(Long customerId, Long orderId) {

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
            if (orderPo.getState().intValue()<=15){  //15为待发货
                logger.debug("customer cancelSelfOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId+" state: "+orderPo.getState());
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setState((byte) 0);//0为订单取消
                orderPoMapper.updateByPrimaryKeySelective(orderPo);
                orderReturnObject=new ReturnObject(ResponseCode.OK,"订单取消成功");
                return orderReturnObject;
            }
            else if (orderPo.getState().equals((byte)18)){
                logger.debug("customer deleteSelfOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId+" state: "+orderPo.getState());
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setBeDeleted((byte) 1);//1为订单逻辑删除
                orderPoMapper.updateByPrimaryKeySelective(orderPo);
                orderReturnObject=new ReturnObject(ResponseCode.OK,"订单删除成功");
                return orderReturnObject;
            }
            else {
                logger.debug("customer deleteSelfOrderById error！the order state: "+orderPo.getState());
                return new ReturnObject(ResponseCode.ORDER_STATENOTALLOW,"订单已发货");
            }

        }
        else{
            logger.debug("customer deleteSelfOrderById error: don't have privilege!   orderId:  "+orderId+"   customerId:  "+customerId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权取消，不是自己订单");
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
            if (orderPo.getState().equals(17)){  //17为待收货
                logger.debug("customer confirmSelfOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId);
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setState((byte)18);//18为已收货
                orderPoMapper.updateByPrimaryKeySelective(orderPo);
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

    public ReturnObject transLateGroToNor(Long customerId, Long id) {
        OrderPo orderPo=null;
        try{
            OrderPoExample example=new OrderPoExample();
            OrderPoExample.Criteria criteria=example.createCriteria();
            criteria.andIdEqualTo(id);
            criteria.andCustomerIdEqualTo(customerId);
            orderPo=(OrderPo) orderPoMapper.selectByExample(example);
        }catch (DataAccessException e){
            logger.error("transLateGroToNor:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (orderPo==null){
            logger.debug("customer transLateGroToNor error: it's empty!  orderId:  "+id+"   customerId:  "+customerId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"订单号不存在");
        }
        else{
            if (orderPo.getState().equals(9)){
                //9未成团
                orderPo.setOrderType((byte) 0);
                orderPoMapper.updateByPrimaryKeySelective(orderPo);

                return new ReturnObject(ResponseCode.OK,"订单确认收货成功");
            }
            else{
                logger.debug("customer transLateGroToNor error！the order state: "+orderPo.getState());
                return new ReturnObject(ResponseCode.ORDER_STATENOTALLOW,"订单状态禁止");
            }
        }
    }

    public ReturnObject<PageInfo<VoObject>> getShopSelfSimpleOrders(Long shopId, Long customerId, String orderSn, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        //设置查询条件
        OrderPoExample example=new OrderPoExample();
        OrderPoExample.Criteria criteria=example.createCriteria();
        criteria.andCustomerIdEqualTo(customerId);
        criteria.andShopIdEqualTo(shopId);
        if (orderSn!=null){
            criteria.andOrderSnEqualTo(orderSn);
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
            logger.error("getShopSelfSimpleOrders:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        List<VoObject> ret=new ArrayList<>(orderPos.size());
        for(OrderPo po:orderPos){
            SimpleOrder simpleOrder=new SimpleOrder(po);
            logger.debug("getShopSelfSimpleOrders: shopId: "+shopId);
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
     * 卖家获得订单详情
     * @param shopId
     * @param id
     * @return 订单详情
     * @author Gang Ye
     * @modify 2020/12/9 by Gang Ye
     *                      将顾客信息与店铺信息查询移到Service
     */
    public ReturnObject<VoObject> getShopSelfOrder(Long shopId, Long id) {
        ReturnObject<VoObject> orderReturnObject=null;

        OrderPo orderPo=null;
        try{
            orderPo=orderPoMapper.selectByPrimaryKey(id);
        }catch (DataAccessException e){
            logger.error("getShopSelfOrder:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (orderPo==null){
            logger.debug("shop getOrderById error: it's empty!  orderId:  "+id+"   shopId:  "+shopId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"订单号不存在");

        }
        else if(orderPo.getShopId().equals(shopId)){
            logger.debug("shop getSelfOrder success！  orderId:  "+id+"   shopId:  "+shopId);
            Order order=new Order(orderPo);


            LocalDateTime time=LocalDateTime.of(1,1,1,1,1,1);

            OrderItemPoExample itemPoExample=new OrderItemPoExample();
            OrderItemPoExample.Criteria criteria=itemPoExample.createCriteria();
            criteria.andOrderIdEqualTo(id);
            List<OrderItemPo> orderItemPos=orderItemPoMapper.selectByExample(itemPoExample);
            ArrayList<SimpleOrderItem> simpleOrderItems=new ArrayList<>(orderItemPos.size());
            for (OrderItemPo orderItemPo : orderItemPos){
                SimpleOrderItem simpleOrderItem=new SimpleOrderItem(orderItemPo);
                simpleOrderItems.add(simpleOrderItem);
            }

            order.setSimpleOrderItemList(simpleOrderItems);
            orderReturnObject=new ReturnObject<>(order);

            return orderReturnObject;
        }
        else{
            logger.debug("shop getSelfOrder error: don't have privilege!   orderId:  "+id+"   shopId:  "+shopId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权访问，不是本店订单");
        }
    }

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
        else if(orderPo.getShopId().equals(shopId)){
            logger.debug("modifyOrderMessage success！  orderId:  "+orderId+"   orderId:  "+orderId);
            orderPo.setGmtModified(LocalDateTime.now());
            orderPo.setMessage(message);
            orderPoMapper.updateByPrimaryKeySelective(orderPo);
            orderReturnObject=new ReturnObject(ResponseCode.OK,"订单留言成功");
            return orderReturnObject;

        }
        else{
            logger.debug("modifyOrderMessage error: don't have privilege!   orderId:  "+orderId+"   shopId:  "+shopId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权修改，不是自己订单");
        }
    }

    public ReturnObject deleteShopOrder(Long shopId, Long orderId) {
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
        else if(orderPo.getShopId().equals(shopId)){
            if (orderPo.getState().equals(15)){  //15为待发货
                logger.debug("deleteShopOrder success！  orderId:  "+orderId+"   shopId:  "+shopId);
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setBeDeleted((byte) 1);
                orderPo.setState((byte) 0);//0为订单取消
                orderPoMapper.updateByPrimaryKeySelective(orderPo);
                orderReturnObject=new ReturnObject(ResponseCode.OK,"订单取消成功");
                return orderReturnObject;
            }
            else {
                logger.debug("deleteShopOrder error！the order state: "+orderPo.getState());
                return new ReturnObject(ResponseCode.ORDER_STATENOTALLOW,"订单已发货");
            }

        }
        else{
            logger.debug("deleteShopOrder error: don't have privilege!   orderId:  "+orderId+"   shopId:  "+shopId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权取消，不是自己订单");
        }
    }

    public ReturnObject deliverShopOrder(Long shopId, Long orderId, String freightSn) {
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
        else if(orderPo.getShopId().equals(shopId)){
            if (orderPo.getState().equals(15)||orderPo.getState().equals(16)){  //15为待发货
                logger.debug("deliverShopOrder success！  orderId:  "+orderId+"   shopId:  "+shopId);
                orderPo.setGmtModified(LocalDateTime.now());
                //orderPo.setFreightSn(freightSn);
                orderPo.setState((byte)16);//16为已发货
                orderPoMapper.updateByPrimaryKeySelective(orderPo);
                orderReturnObject=new ReturnObject(ResponseCode.OK,"订单发货成功");
                return orderReturnObject;
            }
            else {
                logger.debug("deliverShopOrder error！the order state: "+orderPo.getState());
                return new ReturnObject(ResponseCode.ORDER_STATENOTALLOW,"订单状态禁止");
            }

        }
        else{
            logger.debug("deliverShopOrder error: don't have privilege!   orderId:  "+orderId+"   shopId:  "+shopId);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"订单无权修改，不是自己订单");
        }
    }

    public int deductStock(Long skuId, Integer quantity) {
        String key="sku_"+skuId;
        String value= RandomCaptcha.getRandomString(11);
        boolean flag=redisTemplate.opsForValue().setIfAbsent(key,value,Common.addRandomTime(timeout),TimeUnit.SECONDS);
        if (flag){
            logger.debug("redis lock successful!  key:  "+key);
            Integer stock=(Integer) redisTemplate.opsForValue().get(skuId.toString());
            if (stock>quantity){
                redisTemplate.opsForValue().decrement(String.valueOf(skuId),quantity);
                String lockValue= (String) redisTemplate.opsForValue().get(key);
                if (lockValue.equals(value)){
                    redisTemplate.delete(key);
                    logger.debug("redis unlock! key:  "+key);
                }
                return 1;
            }
            String lockValue= (String) redisTemplate.opsForValue().get(key);
            if (lockValue.equals(value)){
                redisTemplate.delete(key);
                logger.debug("redis unlock! key:  "+key);
            }
            return -1;

        }
        return 0;
    }

    public boolean loadGoodsStock(Long skuId, Integer stock) {
        if (timeout <= 0) {
            timeout = 60;
        }

        long min = 1;
        long max = timeout / 5;
        try {
            //增加随机数，防止雪崩
            timeout += (long) new Random().nextDouble() * (max - min);
            redisTemplate.opsForValue().set(String.valueOf(skuId), stock, timeout, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean haveOrderSn(String orderSn) {
        List<String> orderSns=orderMapper.getAllOrderSn();
        return orderSns.contains(orderSn);
    }

    public ReturnObject<Long> insertOrder(OrderPo orderPo) {
        ReturnObject<Long> returnObject=null;
        try{
            Long orderId=(long)orderPoMapper.insert(orderPo);
            logger.debug("insertOrder:  orderId:"+orderId+"    customerId: "+orderPo.getCustomerId());
            returnObject=new ReturnObject<Long>(orderId);
            return returnObject;
        }catch (DataAccessException e){
            logger.error("insertOrder:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
    }

    public ReturnObject insertOrderItem(OrderItemPo orderItemPo) {
        ReturnObject<Long> returnObject=null;
        try{
            orderItemPoMapper.insert(orderItemPo);
            logger.debug("insertOrderItem:  orderId:"+orderItemPo.getOrderId());
            returnObject=new ReturnObject<Long>();
            return returnObject;
        }catch (DataAccessException e){
            logger.error("insertOrderItem:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 获得订单所有状态
     * @param customerId
     * @return 订单状态视图
     * @author Gang Ye
     */
    public ReturnObject<List> getOrderAllStates(Long customerId) {
        ReturnObject<List> returnObject=null;
        try{
            List<Byte> states=orderMapper.getAllOrderStatesByCusId(customerId);
            ArrayList<OrderState> orderStates=new ArrayList<>(states.size());
            for (Byte state:states) {
                OrderState orderState=new OrderState();
                orderState.setCode(state);
                orderState.setName("11");
                orderStates.add(orderState);
            }
            returnObject=new ReturnObject<>(orderStates);
            return returnObject;
        }catch (DataAccessException e){
            logger.error("getAllStates:  DataAccessException:  "+e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

    }
}
