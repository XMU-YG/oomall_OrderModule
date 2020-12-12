package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.Freight;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FreightModelVo {
    private Long id;
    private String name;
    private Byte type;
    private Integer unit;
    private Boolean defaultModel;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Byte getType() {
        return type;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }
    public void setDefaultModel(Boolean defaultModel) {
        this.defaultModel = defaultModel;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }
}
