package cn.edu.xmu.goodsprovider.Module;


import lombok.Data;

import java.io.Serializable;

/**
 * @Author Pinzhen Chen
 * @Date 2020/11/28 22:18
 */
@Data

public class BrandSimpleRetVo implements Serializable {


    private Long id;

    private String name;

    private String imageUrl;

    public BrandSimpleRetVo(){

    }
}
