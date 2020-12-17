package cn.edu.xmu.ooad.order.discount.impl;

import cn.edu.xmu.ooad.order.bo.COrderItem;
import cn.edu.xmu.ooad.order.discount.BaseCouponLimitation;

import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-19
 */
public class AmountCouponLimitation extends BaseCouponLimitation {

	public AmountCouponLimitation(){}

	public AmountCouponLimitation(long value) {
		super(value);
	}

	@Override
	public boolean pass(List<COrderItem> COrderItems) {
		long t = 0;
		for (COrderItem oi : COrderItems) {
			t += oi.getQuantity();
		}
		return t >= value;
	}
}
