package cn.edu.xmu.goodsprovider.Module;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Pinzhen Chen
 * @Date 2020/11/28 22:23
 */
@Data

public class FreightSimpleRetVo implements Serializable {

    private Long id;

    private String name;


    private Byte type;

//    @ApiModelProperty(name = "运费模板名称", value = "default")
//    private Boolean default;


    private String gmtCreate;


    private String gmtModified;

    public FreightSimpleRetVo(){

    }
}
