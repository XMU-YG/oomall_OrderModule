package cn.edu.xmu.freight.model.vo;
import cn.edu.xmu.freight.model.bo.Freight;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class FreightSimpInfoVo {
    @NotBlank(message = "模板名不能为空")
    @ApiModelProperty(value = "模板名称")
    private String name;


    @ApiModelProperty(value = "计重单位")
    private Integer unit;



    public void setName(String name) {
        this.name = name;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public Freight createFreight()
    {
        Freight freight=new Freight();
        freight.setName(name);
        freight.setUnit(unit);
        return freight;
    }
}
