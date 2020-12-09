package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.vo.FreightModelVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.ooad.util.encript.SHA256;
import cn.edu.xmu.freight.model.po.FreightPo;
//import cn.edu.xmu.privilege.model.vo.UserRetVo;
//import cn.edu.xmu.privilege.model.vo.UserSimpleRetVo;
//import cn.edu.xmu.privilege.model.vo.UserVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Freight implements VoObject{
    private Long id;
    private Long shopId;
    private String name;
    private Byte defaultModel;//记得改一下，之后可能是Byte
    private Byte type;
    private Integer unit;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Freight(FreightPo po)
    {
        this.id=po.getId();
        this.shopId=po.getShopId();
        this.name=po.getName();
        this.defaultModel=po.getDefaultModel();
        this.type=po.getType();
        this.unit=po.getUnit();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    public Freight()
    {
    }
    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public void setDefaultModel(Byte defaultModel) {
        this.defaultModel = defaultModel;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
    public FreightPo getFreightPo()
   {
    FreightPo freightPo=new FreightPo();
    freightPo.setId(id);
    freightPo.setShopId(shopId);
    freightPo.setDefaultModel(defaultModel);
    freightPo.setGmtCreate(gmtCreate);
    freightPo.setGmtModified(gmtModified);
    freightPo.setName(name);
    freightPo.setType(type);
    freightPo.setUnit(unit);
    return freightPo;

    }
    @Override
    public Object createVo() {
        FreightModelVo freightModelVo = new FreightModelVo();
        freightModelVo.setId(id);
        freightModelVo.setName(name);
        if(defaultModel==null||defaultModel==0x00)
            freightModelVo.setDefaultModel(false);
        else
            freightModelVo.setDefaultModel(true);
        //freightModelVo.setDefaultModel((defaultModel == 0x00) ? false : true);
        freightModelVo.setType(type);
        freightModelVo.setGmtCreate(gmtCreate);
        freightModelVo.setGmtModified(gmtModified);

        return freightModelVo;
    }
    @Override
    public Object createSimpleVo() {
        return null;
    }


}
