package cn.edu.xmu.order_provider.model.freight;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FreightModelVo {
    private Long id;
    private String name;
    private Byte type;
    private Integer unit;
    private Boolean defaultModel;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public FreightModelVo(){}

}
