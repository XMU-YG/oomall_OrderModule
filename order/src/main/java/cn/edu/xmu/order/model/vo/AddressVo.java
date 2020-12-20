package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.po.OrderPo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 买家修改订单地址信息Vo对象
 */
@Data
@ApiModel
public class AddressVo {
    @NotBlank(message = "收货人不能为空")
    private String consignee;
    @NotNull(message = "地区不能为空")
    private Long regionId;
    @NotBlank(message = "地址不能为空")
    private String address;
    @NotBlank(message = "电话不能为空")
    private String mobile;

    public AddressVo(String consignee, Long regionId, String address, String mobile) {
        this.consignee = consignee;
        this.regionId = regionId;
        this.address = address;
        this.mobile = mobile;
    }

    public OrderPo createPo(){
        OrderPo orderPo=new OrderPo();
        orderPo.setConsignee(this.consignee);
        orderPo.setAddress(this.address);
        orderPo.setMobile(this.mobile);
        orderPo.setRegionId(this.regionId);
        return orderPo;
    }
}
