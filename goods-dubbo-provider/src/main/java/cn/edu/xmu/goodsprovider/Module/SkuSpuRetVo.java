package cn.edu.xmu.goodsprovider.Module;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data

public class SkuSpuRetVo implements Serializable {
    private Long id;
    private String skuSn;
    private String name;
    private String detail;
    private Long originalPrice;
    private String imageUrl;
    private int inventory;
    private boolean disable;
    private Long price;//现价
    private String configuration;
    private Long weight;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private SpuRetVo spu;
}
