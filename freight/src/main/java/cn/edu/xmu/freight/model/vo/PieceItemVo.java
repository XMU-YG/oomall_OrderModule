package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.PieceItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PieceItemVo {

    @ApiModelProperty(value = "抵达地区码")
    private Long regionId;

    @ApiModelProperty(value = "首件数")
    private Integer firstItem;

    @ApiModelProperty(value = "规则首件运费")
    private Long firstItemsPrice;

    @ApiModelProperty(value = "规则续件数")
    private Integer additionalItems;

    @ApiModelProperty(value = "规则续件运费")
    private Long additionalItemsPrice;

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public void setFirstItem(Integer firstItem) {
        this.firstItem = firstItem;
    }

    public void setFirstItemsPrice(Long firstItemsPrice) {
        this.firstItemsPrice = firstItemsPrice;
    }

    public void setAdditionalItems(Integer additionalItems) {
        this.additionalItems = additionalItems;
    }

    public void setAdditionalItemsPrice(Long additionalItemsPrice) {
        this.additionalItemsPrice = additionalItemsPrice;
    }

    public PieceItem createPieceItem()
    {
        PieceItem pieceItem=new PieceItem();
        pieceItem.setRegionId(regionId);
        pieceItem.setFirstItems(firstItem);
        pieceItem.setFirstItemPrice(firstItemsPrice);
        pieceItem.setAdditionalItems(additionalItems);
        pieceItem.setAdditionalItemsPrice(additionalItemsPrice);
        return  pieceItem;
    }
}
