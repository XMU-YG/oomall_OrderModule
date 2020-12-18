package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.goodsprovider.Module.ShopRetVo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Shop {
    //店铺信息
    private Long shopId;

    private String shopName;

    private Byte state;

    private LocalDateTime shopGmtCreate;

    private LocalDateTime shopGmtModified;

    public Shop(){

    }
    public Shop(ShopRetVo shopRetVo){
        this.setShopId(shopRetVo.getId());
        this.setShopName(shopRetVo.getName());
        this.setState(shopRetVo.getState());
        this.setShopGmtCreate(shopRetVo.getGmtCreated());
        this.setShopGmtModified(shopRetVo.getGmtModified());
    }
}
