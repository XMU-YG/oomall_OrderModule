package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.order.model.vo.StateRetVo;
import lombok.Data;

@Data
public class OrderState implements VoObject {
    private int code;
    private String name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public StateRetVo createVo() {
        return new StateRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
