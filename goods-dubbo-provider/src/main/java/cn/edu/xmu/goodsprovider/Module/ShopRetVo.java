package cn.edu.xmu.goodsprovider.Module;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
     *
     * @param  * @param null
     * @return
     * @author 24320182203161 Bai Haoyue
     * @date: 2020/11/29 22:50
     */
@Data
public class ShopRetVo implements Serializable {

    private Long id;

    private String name;

    private Byte state;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;
}
