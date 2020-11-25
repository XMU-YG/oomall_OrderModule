package cn.edu.xmu.order.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.dao.OrderItemDao;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.vo.NewOrderItemVo;
import cn.edu.xmu.order.model.vo.NewOrderRetVo;
import cn.edu.xmu.order.model.vo.NewOrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private Logger logger=LoggerFactory.getLogger(OrderService.class);
    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private FreightDao freightDao;
    @Transactional
    public ReturnObject<VoObject> addNewOrder(NewOrderVo vo){
        ReturnObject<VoObject> returnObject=null;
        NewOrderRetVo newOrderRetVo=null;
        ArrayList<OrderItemPo> orderItemPos=null;//order_items列表
        List<NewOrderItemVo> newOrderItemVoList=vo.getOrderItems();
        //获得OrderItems，库存不足直接返回
        for (NewOrderItemVo orderItem:newOrderItemVoList) {
            /**根据商品skuId获得商品详细信息，返回类型为OrderItemPo,需要商品模块查询接口
             * param skuId
             * return OrderItemPo
             * 商品模块
             */

            OrderItemPo goodsItemPo=goodsService.getGoodsBySkuId(orderItem.getSkuId());
            goodsItemPo.setCouponActivityId(orderItem.getCouponActId());
            //库存不足
            if (goodsItemPo.getQuantity()<orderItem.getQuantity()){
                logger.debug("goods stock isn't enough. goods skuId:  "+goodsItemPo.getGoodsSkuId()+"  stock:  "+goodsItemPo.getQuantity()+"    needed:  "+orderItem.getQuantity());
                returnObject=new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
                return  returnObject;
            }
            else{
                orderItemPos.add(goodsItemPo);
            }
        };
        //计算订单原价
        newOrderRetVo.setOriginPrice(orderItemDao.getOrderPrice(orderItemPos));
        //计算运费
        newOrderRetVo.setFreightPrice(freightDao.getFreight(orderItemPos));
        //计算优惠
        newOrderRetVo.setDiscountPrice(orderItemDao.getDiscountPrice(orderItemPos));
        //计算返点
        newOrderRetVo.setRebateNum(rebateDao.getRebate());
        //计算团购优惠
        newOrderRetVo.setGrouponDiscount(groupDao.getGrouponDiscountById(vo.getGrouponId()));



        newOrderRetVo.createdByVo(vo);



        returnObject=new ReturnObject<>((VoObject) newOrderRetVo);
        return returnObject;
    }
}
