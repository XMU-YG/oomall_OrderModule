package cn.edu.xmu.ooad.order.discount;

import cn.edu.xmu.ooad.order.bo.COrderItem;

import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-18
 */
public interface Computable {

	List<COrderItem> compute(List<COrderItem> COrderItems);
}
