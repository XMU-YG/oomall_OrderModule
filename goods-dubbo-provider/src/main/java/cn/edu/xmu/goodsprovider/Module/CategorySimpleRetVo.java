package cn.edu.xmu.goodsprovider.Module;


import lombok.Data;

import java.io.Serializable;

/**
 * @Author Pinzhen Chen
 * @Date 2020/11/28 22:21
 */
@Data

public class CategorySimpleRetVo implements Serializable {

    private Long id;

    private String name;

    public CategorySimpleRetVo() {

    }
}
