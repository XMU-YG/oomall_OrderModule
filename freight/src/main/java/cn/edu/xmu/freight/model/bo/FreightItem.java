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

    private  Long freightModelId;

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

    public FreightItem()
    {

    }

    public FreightItem(WeightFreightPo po){
        this.id=po.getId();
        this.freightModelId=po.getFreightModelId();
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

    public Long getId() {
        return id;
    }

    public Long getFreightModelId() {
        return freightModelId;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setFreightModelId(Long freightModelId) {
        this.freightModelId = freightModelId;
    }

    public void setFirstWeight(Long firstWeight) {
        this.firstWeight = firstWeight;
    }

    public void setFirstWeightFreight(Long firstWeightFreight) {
        this.firstWeightFreight = firstWeightFreight;
    }

    public void setTenPrice(Long tenPrice) {
        this.tenPrice = tenPrice;
    }

    public void setFiftyPrice(Long fiftyPrice) {
        this.fiftyPrice = fiftyPrice;
    }

    public void setHundredPrice(Long hundredPrice) {
        this.hundredPrice = hundredPrice;
    }

    public void setTrihunPrice(Long trihunPrice) {
        this.trihunPrice = trihunPrice;
    }

    public void setAbovePrice(Long abovePrice) {
        this.abovePrice = abovePrice;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public WeightFreightPo getWeightFreightPo()
    {
        WeightFreightPo weightFreightPo=new WeightFreightPo();
        weightFreightPo.setId(id);
        weightFreightPo.setFreightModelId(freightModelId);
        weightFreightPo.setFirstWeight(firstWeight);
        weightFreightPo.setFirstWeightFreight(firstWeightFreight);
        weightFreightPo.setTenPrice(tenPrice);
        weightFreightPo.setFiftyPrice(fiftyPrice);
        weightFreightPo.setHundredPrice(hundredPrice);
        weightFreightPo.setTrihunPrice(trihunPrice);
        weightFreightPo.setAbovePrice(abovePrice);
        weightFreightPo.setRegionId(regionId);
        weightFreightPo.setGmtCreate(gmtCreate);
        weightFreightPo.setGmtModified(gmtModified);
        return weightFreightPo;

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
