package cn.edu.xmu.order.service.impl;

public interface DeductStockImpl {
    public boolean deductStock(Long skuId,Integer quantity);
}
