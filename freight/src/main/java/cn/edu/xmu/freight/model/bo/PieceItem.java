package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.po.PieceFreightPo;
import cn.edu.xmu.freight.model.vo.PieceFreightRetVo;
import cn.edu.xmu.ooad.model.VoObject;

import java.time.LocalDateTime;

/**
 * 件数运费模板的详细信息
 * author ShiYu Liao
 * create 2020/12/7
 * modify 2020/12/7
 */
public class PieceItem implements VoObject {

    private Long id;
    private Long regionId;
    private Integer firstItem;
    private Long firstItemPrice;
    private Integer additionalItems;
    private Long additionalItemsPrice;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Long getId() {
        return id;
    }

    public Long getRegionId() {
        return regionId;
    }

    public Integer getFirstItem() {
        return firstItem;
    }

    public Long getFirstItemPrice() {
        return firstItemPrice;
    }

    public Integer getAdditionalItems() {
        return additionalItems;
    }

    public Long getAdditionalItemsPrice() {
        return additionalItemsPrice;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public PieceItem(PieceFreightPo po) {
        this.id = po.getId();
        this.regionId = po.getRegionId();
        this.firstItem = po.getFirstItems();
        this.firstItemPrice = po.getFirstItemsPrice();
        this.additionalItems = po.getAdditionalItems();
        this.additionalItemsPrice = po.getAdditionalItemsPrice();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    @Override
    public Object createVo() {
        return new PieceFreightRetVo(this);
    }
    @Override
    public Object createSimpleVo() {
        return null;
    }
}