package cn.edu.xmu.order.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.order.mapper.OrderPoMapper;
import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.bo.SimpleOrder;
import cn.edu.xmu.order.model.bo.SimpleOrderItem;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderItemPoExample;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.po.OrderPoExample;
import cn.edu.xmu.order.model.vo.AddressVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderDao {

    private  static  final Logger logger = LoggerFactory.getLogger(OrderDao.class);
    @Autowired
    private OrderPoMapper orderPoMapper;

    private OrderItemPoMapper orderItemPoMapper;

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
     * @modified 2020/11/26 by Gang Ye
     */
    public ReturnObject<PageInfo<VoObject>> getAllSimpleOrders(Long customerId,String orderSn, Integer state, String beginTime, String endTime, Integer page, Integer pageSize){

        //设置查询条件
        OrderPoExample example=new OrderPoExample();
        OrderPoExample.Criteria criteria=example.createCriteria();
        criteria.andCustomerIdEqualTo(customerId);
        criteria.andBeDeletedEqualTo((byte) 0);//未逻辑删除
        criteria.andOrderSnEqualTo(orderSn);
        criteria.andStateEqualTo(state.byteValue());
        //转换日期格式 String-》LocalDateTime
        DateTimeFormatter df=DateTimeFormatter.ofPattern("yyyy-mm-dd hh-mm-ss");
        LocalDateTime begin=LocalDateTime.parse(beginTime,df);
        LocalDateTime end=LocalDateTime.parse(endTime,df);
        criteria.andConfirmTimeBetween(begin,end);

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
     */
    public ReturnObject getOrderById(Long customerId,Long orderId){
        ReturnObject<Order> orderReturnObject=null;

        OrderPo orderPo=null;
        try{
            orderPo=orderPoMapper.selectByPrimaryKey(orderId);
        }catch (DataAccessException e){
            logger.error("getOrderById:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if (orderPo==null||orderPo.getBeDeleted().equals(1)){
            logger.debug("customer getOrderById error: it's empty!  orderId:  "+orderId+"   customerId:  "+customerId);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"订单号不存在");

        }
        else if(orderPo.getCustomerId().equals(customerId)){
            logger.debug("customer getOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId);
            Order order=new Order(orderPo);

            order.setCustomerUserName("123");
            order.setCustomerRealName("123");
            order.setShopName("123");
            LocalDateTime time=LocalDateTime.of(1,1,1,1,1,1);
            order.setShopGmtCreate(time);
            order.setShopGmtModified(time);

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
            if (orderPo.getState().equals(0)){  //0为未发货
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
            if (orderPo.getState().equals(0)){  //0为未发货
                logger.debug("customer deleteSelfOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId);
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setBeDeleted((byte) 1);
                orderPoMapper.updateByPrimaryKeySelective(orderPo);
                orderReturnObject=new ReturnObject(ResponseCode.OK,"订单取消成功");
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
            if (orderPo.getState().equals(1)){  //1为待收货
                logger.debug("customer confirmSelfOrderById success！  orderId:  "+orderId+"   customerId:  "+customerId);
                orderPo.setGmtModified(LocalDateTime.now());
                orderPo.setState((byte)2);//2为已收货
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
}
