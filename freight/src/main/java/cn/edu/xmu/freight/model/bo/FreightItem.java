package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.po.WeightFreightPo;
import cn.edu.xmu.freight.model.vo.WeightFreightRetVo;
import cn.edu.xmu.ooad.model.VoObject;

import java.time.LocalDateTime;

/**
 * 重量运费模板的详细信息
 * author ShiYu Liao
 * create 2020/12/5
 * modify 2020/12/5
 */
public class FreightItem implements VoObject {

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

    public Long getId() {
        return id;
    }

    public Long getFirstWeight() {
        return firstWeight;
    }

    public Long getFirstWeightFreight() {
        return firstWeightFreight;
    }

    public Long getTenPrice() {
        return tenPrice;
    }

    public Long getFiftyPrice() {
        return fiftyPrice;
    }

    public Long getHundredPrice() {
        return hundredPrice;
    }

    public Long getTrihunPrice() {
        return trihunPrice;
    }

    public Long getAbovePrice() {
        return abovePrice;
    }

    public Long getRegionId() {
        return regionId;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public FreightItem(WeightFreightPo po){
        this.id=po.getId();
        this.firstWeight=po.getFirstWeight();
        this.firstWeightFreight=po.getFirstWeightFreight();
        this.tenPrice=po.getTenPrice();
        this.fiftyPrice=po.getFiftyPrice();
        this.hundredPrice=po.getHundredPrice();
        this.trihunPrice=po.getTrihunPrice();
        this.abovePrice=po.getAbovePrice();
        this.regionId=po.getRegionId();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    @Override
    public Object createVo() {
        return new WeightFreightRetVo(this);
    }
    @Override
    public Object createSimpleVo() {
        return null;
    }
}
