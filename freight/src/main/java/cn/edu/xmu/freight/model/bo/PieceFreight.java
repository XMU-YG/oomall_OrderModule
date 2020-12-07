package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.vo.PieceModelItemVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.ooad.util.encript.SHA256;
import cn.edu.xmu.freight.model.po.PieceFreightPo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
public class PieceFreight implements VoObject{
    private Long id;
    private Long freightModelId;
    private Integer firstItems;
    private Long firstItemsPrice;
    private Integer additionalItems;
    private Long additionalItemsPrice;
    private Long regionId;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public PieceFreight()
    {
    }

    public PieceFreight(PieceFreightPo po)
    {
        this.id=po.getId();
        this.freightModelId=po.getFreightModelId();
        this.firstItems=po.getFirstItems();
        this.firstItemsPrice=po.getFirstItemsPrice();
        this.additionalItems=po.getAdditionalItems();
        this.additionalItemsPrice=po.getAdditionalItemsPrice();
        this.regionId=po.getRegionId();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    public PieceFreightPo getPieceFreightPo()
    {
        PieceFreightPo po=new PieceFreightPo();
        po.setId(id);
        po.setFreightModelId(freightModelId);
        po.setFirstItems(firstItems);
        po.setFirstItemsPrice(firstItemsPrice);
        po.setAdditionalItems(additionalItems);
        po.setFirstItemsPrice(firstItemsPrice);
        po.setRegionId(regionId);
        po.setGmtCreate(gmtCreate);
        po.setGmtModified(gmtModified);
        return po;
    }
    @Override
    public Object createVo()
    {
      PieceModelItemVo vo=new PieceModelItemVo();
      vo.setAdditionalItems(additionalItems);
      vo.setAdditionalItemsPrice(additionalItemsPrice);
      vo.setFirstItems(firstItems);
      vo.setRegionId(regionId);
      vo.setFirstItemsPrice(firstItemsPrice);
      return vo;
    }
    @Override
    public Object createSimpleVo()
    {
        return null;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public void setAdditionalItems(Integer additionalItems) {
        this.additionalItems = additionalItems;
    }

    public void setAdditionalItemsPrice(Long additionalItemsPrice) {
        this.additionalItemsPrice = additionalItemsPrice;
    }

    public void setFirstItems(Integer firstItems) {
        this.firstItems = firstItems;
    }

    public void setFirstItemsPrice(Long firstItemsPrice) {
        this.firstItemsPrice = firstItemsPrice;
    }

    public void setFreightModelId(Long freightModelId) {
        this.freightModelId = freightModelId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

}
