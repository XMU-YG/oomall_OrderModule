package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.FreightItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class WeightItemVo {

    //@NotBlank(message = "不能为空")
    @ApiModelProperty(value = "首重")
    private Long firstWeight;

    @ApiModelProperty(value = "首重价格")
    private Long firstWeightFreight;

    @ApiModelProperty(value = "10kg以下每0.5kg价格")
    private Long tenPrice;

    @ApiModelProperty(value = "50kg以下每0.5kg价格")
    private Long fiftyPrice;

    @ApiModelProperty(value = "100kg以下每0.5kg价格")
    private Long hundredPrice;

    @ApiModelProperty(value = "300kg以下每0.5kg价格")
    private Long trihunPrice;

    @ApiModelProperty(value = "300kg以上每0.5kg价格")
    private Long abovePrice;

    @ApiModelProperty(value = "抵达地区码")
    private Long regionId;

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

    public FreightItem createFreightItem()
    {
        FreightItem freightItem=new FreightItem();
        freightItem.setFirstWeight(firstWeight);
        freightItem.setFirstWeightFreight(firstWeightFreight);
        freightItem.setTenPrice(tenPrice);
        freightItem.setFiftyPrice(fiftyPrice);
        freightItem.setHundredPrice(hundredPrice);
        freightItem.setTrihunPrice(trihunPrice);
        freightItem.setAbovePrice(abovePrice);
        freightItem.setRegionId(regionId);
        return freightItem;
    }
}
