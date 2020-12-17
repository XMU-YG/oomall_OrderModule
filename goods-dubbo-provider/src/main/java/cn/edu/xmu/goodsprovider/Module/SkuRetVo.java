package cn.edu.xmu.goodsprovider.Module;


import lombok.Data;

import java.io.Serializable;

@Data

public class SkuRetVo implements Serializable {
    private Long id;
    private String skuSn;
    private String name;
    private Long originalPrice;
    private String imageUrl;
    private int inventory;
    private boolean disable;
    private Long price;//现价

    public SkuRetVo() {
    }
}
