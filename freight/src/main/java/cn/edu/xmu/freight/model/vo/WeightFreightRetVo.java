package cn.edu.xmu.freight.model.vo;


import cn.edu.xmu.freight.model.bo.FreightItem;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeightFreightRetVo {

    private Long id;

    private Long firstWeight;

    private Long firstWeightFreight;

    private  Long tenPrice;

    private Long fiftyPrice;

    private Long hundredPrice;

    private Long trihunPrice;

    private Long abovePrice;

    private Long regionId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public WeightFreightRetVo(FreightItem freightItem) {
        this.id = freightItem.getId();
        this.firstWeight = freightItem.getFirstWeight();
        this.firstWeightFreight = freightItem.getFirstWeightFreight();
        this.tenPrice = freightItem.getTenPrice();
        this.fiftyPrice = freightItem.getFiftyPrice();
        this.hundredPrice = freightItem.getHundredPrice();
        this.trihunPrice = freightItem.getTrihunPrice();
        this.abovePrice = freightItem.getAbovePrice();
        this.regionId = freightItem.getRegionId();
        this.gmtCreate = freightItem.getGmtCreate();
        this.gmtModified = freightItem.getGmtModified();
    }
}
