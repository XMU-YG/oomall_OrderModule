package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.PieceItem;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PieceFreightRetVo {
    private Long id;
    private Long regionId;
    private Integer firstItem;
    private Long firstItemPrice;
    private Integer additionalItems;
    private Long additionalItemsPrice;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public PieceFreightRetVo(PieceItem PieceItem ) {
        this.id = PieceItem.getId();
        this.regionId = PieceItem.getRegionId();
        this.firstItem = PieceItem.getFirstItem();
        this.firstItemPrice = PieceItem.getFirstItemPrice();
        this.additionalItems = PieceItem.getAdditionalItems();
        this.additionalItemsPrice = PieceItem.getAdditionalItemsPrice();
        this.gmtCreate = PieceItem.getGmtCreate();
        this.gmtModified =PieceItem.getGmtModified() ;
    }
}
