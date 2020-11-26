package cn.edu.xmu.order.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.mapper.OrderPoMapper;
import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.bo.SimpleOrder;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.po.OrderPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import sun.java2d.pipe.SpanShapeRenderer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderDao {

    private  static  final Logger logger = LoggerFactory.getLogger(OrderDao.class);
    @Autowired
    private OrderPoMapper orderPoMapper;

    public ReturnObject<PageInfo<VoObject>> getAllSimpleOrders(String orderSn, Integer state, String beginTime, String endTime, Integer page, Integer pageSize){

        //设置查询条件
        OrderPoExample example=new OrderPoExample();
        OrderPoExample.Criteria criteria=example.createCriteria();
        criteria.andBeDeletedEqualTo((byte) 0);
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

}
