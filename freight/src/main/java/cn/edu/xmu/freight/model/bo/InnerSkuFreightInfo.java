package cn.edu.xmu.freight.model.bo;

import lombok.Data;

@Data
public class InnerSkuFreightInfo {
    private Long skuId;
    private Long freightId;
    private Long weight;
    private Long shopId;

    public Long getShopId() {
        return shopId;
    }

    public Long getFreightId() {
        return freightId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public Long getWeight() {
        return weight;
    }
}

