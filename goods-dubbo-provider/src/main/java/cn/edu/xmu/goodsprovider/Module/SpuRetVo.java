package cn.edu.xmu.goodsprovider.Module;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * spu 返回vo对象
 * @Author Pinzhen Chen
 * @Date 2020/11/24 20:07
 */
@Data
public class SpuRetVo implements Serializable {


    private Long id;

    private String name;

    private BrandSimpleRetVo brand;

    private CategorySimpleRetVo category;

    private FreightSimpleRetVo freight;

    private ShopSimpleRetVo shop;

    private String goodsSn;
    private String detail;
    private String imageUrl;
    private Byte state;

    private String spec;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Byte disable;

    private List<SkuRetVo> skuList;

}
