package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.po.OrderPo;

/**
 * 买家修改订单地址信息Vo对象
 */
public class AddressVo {
    private String consignee;
    private Long regionId;
    private String address;
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
