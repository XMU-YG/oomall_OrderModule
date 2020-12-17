package cn.edu.xmu.order_provider.model;


import lombok.Data;



import java.util.List;

@Data
public class OrderVo {


    private List<OrderItemVo> orderItems;

    private String consignee;

    private Long regionId;

    private String address;

    private String mobile;

    private String message;

    private Long couponId;

    private Long presaleId;

    private Long grouponId;



}
