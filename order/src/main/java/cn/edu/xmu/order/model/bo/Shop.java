package cn.edu.xmu.order.model.bo;

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

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public LocalDateTime getShopGmtCreate() {
        return shopGmtCreate;
    }

    public void setShopGmtCreate(LocalDateTime shopGmtCreate) {
        this.shopGmtCreate = shopGmtCreate;
    }

    public LocalDateTime getShopGmtModified() {
        return shopGmtModified;
    }

    public void setShopGmtModified(LocalDateTime shopGmtModified) {
        this.shopGmtModified = shopGmtModified;
    }
}
