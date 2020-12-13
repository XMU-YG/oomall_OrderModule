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
<<<<<<< Updated upstream
            if (orderPo.getState()==OrderStatus.AFTER_SALE_UNSHIPPED.getCode()){  //15为未发货
=======
<<<<<<< Updated upstream
            if (orderPo.getState().equals(15)){  //15为未发货
=======
            if (orderPo.getSubstate()==OrderStatus.PAID_SUCCEED.getCode()){  //未发货
>>>>>>> Stashed changes
>>>>>>> Stashed changes
                logger.debug("customer modifySelfOrderAddressById success！  orderId:  "+orderId+"   customerId:  "+customerId);
                OrderPo po=vo.createPo();
                po.setGmtModified(LocalDateTime.now());
                po.setId(orderId);
                orderPoMapper.updateByPrimaryKeySelective(po);
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
<<<<<<< Updated upstream
            if (orderPo.getState()<=OrderStatus.AFTER_SALE_UNSHIPPED.getCode()){  //15为待发货
                logger.debug("customer cancelSelfOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId+" state: "+orderPo.getState());
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setState((byte)OrderStatus.ORDER_CANCEL.getCode());//0为订单取消
=======
<<<<<<< Updated upstream
            if (orderPo.getState().intValue()<=15){  //15为待发货
                logger.debug("customer cancelSelfOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId+" state: "+orderPo.getState());
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setState((byte) 0);//0为订单取消
=======
            if (orderPo.getState()==OrderStatus.WAIT_FOR_PAID.getCode()
                    ||orderPo.getSubstate()==OrderStatus.PAID_SUCCEED.getCode()){  //待付款 未发货
                logger.debug("customer cancelSelfOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId+" state: "+orderPo.getState());
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setState((byte)OrderStatus.CANCELED.getCode());//订单取消
>>>>>>> Stashed changes
>>>>>>> Stashed changes
                orderPoMapper.updateByPrimaryKeySelective(orderPo);
                orderReturnObject=new ReturnObject(ResponseCode.OK,"订单取消成功");
                return orderReturnObject;
            }
<<<<<<< Updated upstream
            else if (orderPo.getState()==OrderStatus.CLIENT_RECEIVED.getCode()){
=======
<<<<<<< Updated upstream
            else if (orderPo.getState().equals((byte)18)){
=======
            else if (orderPo.getState()==OrderStatus.FINISHED.getCode()){ //已完成
>>>>>>> Stashed changes
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
            if (orderPo.getState()==OrderStatus.GOODS_ARRIVED.getCode()){  //17为待收货
                logger.debug("customer confirmSelfOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId);
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setState((byte)OrderStatus.CLIENT_RECEIVED.getCode());//18为已收货
=======
<<<<<<< Updated upstream
            if (orderPo.getState().equals(17)){  //17为待收货
                logger.debug("customer confirmSelfOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId);
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setState((byte)18);//18为已收货
=======
            if (orderPo.getState()==OrderStatus.WAIT_FOR_RECEIVE.getCode()){  //为待收货
                logger.debug("customer confirmSelfOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId);
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setState((byte)OrderStatus.FINISHED.getCode());//已收货
>>>>>>> Stashed changes
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
            if (orderPo.getSubstate()!=null&&orderPo.getSubstate()==OrderStatus.GROUP_FAILED.getCode()){
                //9未成团
                orderPo.setSubstate(null);
=======
<<<<<<< Updated upstream
            if (orderPo.getState().equals(9)){
                //9未成团
=======
            if (orderPo.getSubstate()!=null&&orderPo.getSubstate()==OrderStatus.UNGROUP.getCode()){
                //未成团
                orderPo.setSubstate(null);
>>>>>>> Stashed changes
>>>>>>> Stashed changes
                orderPo.setOrderType((byte) 0);
                orderPoMapper.updateByPrimaryKeySelective(orderPo);

                return new ReturnObject(ResponseCode.OK);
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
        criteria.andShopIdEqualTo(shopId);
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
        else if(orderPo.getShopId()!=null&&orderPo.getShopId().equals(shopId)){
            logger.debug("shop getSelfOrder success！  orderId:  "+id+"   shopId:  "+shopId);
            Order order=new Order(orderPo);
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
        else if(orderPo.getShopId()!=null&&orderPo.getShopId().equals(shopId)){
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
<<<<<<< Updated upstream
        else if(orderPo.getShopId()!=null&&orderPo.getShopId().equals(shopId)){
            if (orderPo.getState()==OrderStatus.AFTER_SALE_UNSHIPPED.getCode()){  //15为待发货
                logger.debug("deleteShopOrder success！  orderId:  "+orderId+"   shopId:  "+shopId);
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setBeDeleted((byte) 1);
                orderPo.setState((byte) OrderStatus.ORDER_CANCEL.getCode());//0为订单取消
=======
<<<<<<< Updated upstream
        else if(orderPo.getShopId().equals(shopId)){
            if (orderPo.getState().equals(15)){  //15为待发货
                logger.debug("deleteShopOrder success！  orderId:  "+orderId+"   shopId:  "+shopId);
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setBeDeleted((byte) 1);
                orderPo.setState((byte) 0);//0为订单取消
=======
        else if(orderPo.getShopId()!=null&&orderPo.getShopId().equals(shopId)){
            if (orderPo.getState()==OrderStatus.PAID_SUCCEED.getCode()){  //待发货
                logger.debug("deleteShopOrder success！  orderId:  "+orderId+"   shopId:  "+shopId);
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setBeDeleted((byte) 1);
                orderPo.setState((byte) OrderStatus.CANCELED.getCode());//0为订单取消
>>>>>>> Stashed changes
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        else if(orderPo.getShopId()!=null&&orderPo.getShopId().equals(shopId)){
            if (orderPo.getState()==OrderStatus.AFTER_SALE_UNSHIPPED.getCode()||
                    orderPo.getState()==OrderStatus.SHIPPING.getCode()){  //15为待发货
                logger.debug("deliverShopOrder success！  orderId:  "+orderId+"   shopId:  "+shopId);
                orderPo.setGmtModified(LocalDateTime.now());
                //orderPo.setFreightSn(freightSn);
                orderPo.setState((byte)OrderStatus.SHIPPING.getCode());//16为已发货
=======
<<<<<<< Updated upstream
        else if(orderPo.getShopId().equals(shopId)){
            if (orderPo.getState().equals(15)||orderPo.getState().equals(16)){  //15为待发货
                logger.debug("deliverShopOrder success！  orderId:  "+orderId+"   shopId:  "+shopId);
                orderPo.setGmtModified(LocalDateTime.now());
                //orderPo.setFreightSn(freightSn);
                orderPo.setState((byte)16);//16为已发货
=======
        else if(orderPo.getShopId()!=null&&orderPo.getShopId().equals(shopId)){
            if (orderPo.getState()==OrderStatus.PAID_SUCCEED.getCode()){  //15为待发货
                logger.debug("deliverShopOrder success！  orderId:  "+orderId+"   shopId:  "+shopId);
                orderPo.setGmtModified(LocalDateTime.now());
                //orderPo.setFreightSn(freightSn);
                orderPo.setState((byte)OrderStatus.WAIT_FOR_RECEIVE.getCode());//已发货
>>>>>>> Stashed changes
>>>>>>> Stashed changes
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

    /**
     * 秒杀扣库存
     * @param skuId
     * @param quantity
     * @return
     */
    public int deductStock(Long skuId, Integer quantity) {
        String key="sku_"+skuId;
        String value= RandomCaptcha.getRandomString(11);
<<<<<<< Updated upstream
        boolean flag=redisTemplate.opsForValue().setIfAbsent(key,value,Common.addRandomTime(timeout),TimeUnit.SECONDS);
        if (flag){
            logger.debug("redis lock successful!  key:  "+key);
            Integer stock=(Integer) redisTemplate.opsForValue().get(skuId.toString());
            stock=10;
            if (stock>quantity){
                redisTemplate.opsForValue().decrement(String.valueOf(skuId),quantity);
=======
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
>>>>>>> Stashed changes
                String lockValue= (String) redisTemplate.opsForValue().get(key);
                if (lockValue.equals(value)){
                    redisTemplate.delete(key);
                    logger.debug("redis unlock! key:  "+key);
                }
            }
<<<<<<< Updated upstream
            return -1;

        }
        else{
            logger.debug("get lock error");
        }
        return 0;
=======
            return 0;
        }catch (NullPointerException e){
            logger.error("redis error!"+e.getMessage());
            return 0;
        }

>>>>>>> Stashed changes
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

    /**
     * 判断订单号是否重复
     * @param orderSn
     * @return
     */
    public boolean haveOrderSn(String orderSn) {
        List<String> orderSns=orderMapper.getAllOrderSn();
        return orderSns.contains(orderSn);
    }

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
     * 根据订单号获得OrderPo
     * @param id
     * @return OrderPo
     */
    public OrderPo getOrderPoById(Long id){
        return orderPoMapper.selectByPrimaryKey(id);
    }


<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
    }
=======

>>>>>>> Stashed changes
>>>>>>> Stashed changes
}
