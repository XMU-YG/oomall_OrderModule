package cn.edu.xmu.ooad.order.discount.impl;

import cn.edu.xmu.ooad.order.bo.COrderItem;
import cn.edu.xmu.ooad.order.discount.BaseCouponDiscount;
import cn.edu.xmu.ooad.order.discount.BaseCouponLimitation;

import java.util.List;

public class CheapestPercentageDiscount extends BaseCouponDiscount {

	public CheapestPercentageDiscount() {

	}

	public CheapestPercentageDiscount(BaseCouponLimitation limitation, long value) {
		super(limitation, value);
	}

	@Override
	public void calcAndSetDiscount(List<COrderItem> COrderItems) {
		int min = Integer.MAX_VALUE;
		int total = 0;
		for (int i = 0; i < COrderItems.size(); i++) {
			COrderItem oi = COrderItems.get(i);
			total += oi.getPrice() * oi.getQuantity();
			if (oi.getPrice() < min) {
				min = i;
			}
		}

		long discount = (long) ((1.0 * value / 100) * COrderItems.get(min).getPrice());

		for (COrderItem oi : COrderItems) {
			oi.setDiscount((long) ((1.0 * oi.getPrice() * oi.getQuantity()) / total * discount / oi.getQuantity()));
		}
	}
}
