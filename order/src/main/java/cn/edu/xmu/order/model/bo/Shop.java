package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.goodsprovider.Module.ShopRetVo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Shop {
    //店铺信息
    private Long id;

    private String name;

    private Byte state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public Shop(){

    }
    public Shop(ShopRetVo shopRetVo){
        this.setId(shopRetVo.getId());
        this.setName(shopRetVo.getName());
        this.setState(shopRetVo.getState());
        this.setGmtCreate(shopRetVo.getGmtCreated());
        this.setGmtModified(shopRetVo.getGmtModified());
    }
}
