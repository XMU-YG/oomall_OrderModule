package cn.edu.xmu.order.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.mapper.OrderItemPoMapper;

import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderItemPoExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderItemDao {


    private  static  final Logger logger = LoggerFactory.getLogger(OrderItemDao.class);

    @Autowired
    private OrderItemPoMapper orderItemPoMapper;

    public OrderItemPo getOrderItemById(Long id) {
        return orderItemPoMapper.selectByPrimaryKey(id);
    }


    public int updateOrderItem(OrderItemPo orderItemPo) {
        return orderItemPoMapper.updateByPrimaryKey(orderItemPo);
    }

    public ReturnObject insertOrderItem(OrderItemPo orderItemPo) {
        ReturnObject returnObject=null;
        try{
            int ret=orderItemPoMapper.insert(orderItemPo);
            if (ret==1){
                logger.debug("insertOrderItem:  orderId:"+orderItemPo.getOrderId()+"  id: "+orderItemPo.getId());
                returnObject=new ReturnObject<>();
                return returnObject;
            }else{
                logger.debug("insertOrderItem error:  orderId:"+orderItemPo.getOrderId()+"  id: "+orderItemPo.getId());
                returnObject=new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
                return returnObject;
            }

        }catch (DataAccessException e){
            logger.error("insertOrderItem:  DataAccessException:  "+e.getMessage());
            return  new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
    }

    public List<OrderItemPo> getOrderItemsByOrderId(Long orderId) {
        OrderItemPoExample example=new OrderItemPoExample();
        OrderItemPoExample.Criteria criteria=example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);

        return orderItemPoMapper.selectByExample(example);
    }

    public List<OrderItemPo> getItemsBySkuId(Long skuId) {
        List<OrderItemPo> orderItemPos=null;
        OrderItemPoExample example=new OrderItemPoExample();
        OrderItemPoExample.Criteria criteria=example.createCriteria();
        criteria.andGoodsSkuIdEqualTo(skuId);
        orderItemPos=orderItemPoMapper.selectByExample(example);
        return orderItemPos;
    }

}
