package cn.edu.xmu.goodsprovider.Module;


import lombok.Data;

import java.io.Serializable;

/**
 * @Author Pinzhen Chen
 * @Date 2020/11/28 22:49
 */
@Data

public class ShopSimpleRetVo implements Serializable {

    private Long id;


    private String name;

    public ShopSimpleRetVo(){

    }
}
