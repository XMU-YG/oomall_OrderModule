package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.Freight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "ItemsVo", description = "商品")
public class ItemsVo implements Serializable {
    @ApiModelProperty(value = "商品id")
    private Long skuId;

    @ApiModelProperty(value = "数量")
    private Long count;

    public Long getCount() {
        return count;
    }

    public Long getSkuId() {
        return skuId;
    }
}