package cn.edu.xmu.order.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class FreightSnVo {
    @NotBlank
    private String freightSn;
}
