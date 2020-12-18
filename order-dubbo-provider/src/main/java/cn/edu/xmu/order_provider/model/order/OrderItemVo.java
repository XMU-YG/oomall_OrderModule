package cn.edu.xmu.order_provider.model.order;

import lombok.Data;

import java.io.Serializable;


@Data
public class OrderItemVo implements Serializable {

    private Long skuId;

    private int quantity;

}
