package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.PieceFreight;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class PieceModelItemVo {

    @ApiModelProperty(value = "首件数")
    private Integer firstItems;

    @ApiModelProperty(value = "首费")
    private Long firstItemsPrice;

    @ApiModelProperty(value = "续件数")
    private Integer additionalItems;

    @ApiModelProperty(value = "续费")
    private Long additionalItemsPrice;

    @ApiModelProperty(value = "地区ID")
    private Long regionId;

    public PieceFreight createPieceFreight()
    {
        PieceFreight pieceFreight=new PieceFreight();
        pieceFreight.setFirstItems(firstItems);
        pieceFreight.setFirstItemsPrice(firstItemsPrice);
        pieceFreight.setAdditionalItems(additionalItems);
        pieceFreight.setAdditionalItemsPrice(additionalItemsPrice);
        pieceFreight.setRegionId(regionId);
        return pieceFreight;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public void setFirstItemsPrice(Long firstItemsPrice) {
        this.firstItemsPrice = firstItemsPrice;
    }

    public void setFirstItems(Integer firstItems) {
        this.firstItems = firstItems;
    }

    public void setAdditionalItemsPrice(Long additionalItemsPrice) {
        this.additionalItemsPrice = additionalItemsPrice;
    }

    public void setAdditionalItems(Integer additionalItems) {
        this.additionalItems = additionalItems;
    }
}
