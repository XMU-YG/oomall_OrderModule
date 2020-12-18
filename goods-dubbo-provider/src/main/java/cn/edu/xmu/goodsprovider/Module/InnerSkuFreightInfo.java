package cn.edu.xmu.goodsprovider.Module;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Pinzhen Chen
 * @Date 2020/12/11 13:46
 */
@Data
public class InnerSkuFreightInfo implements Serializable {
    private Long skuId;
    private Long freightId;
    private Long weight;
    private Long shopId;
}
