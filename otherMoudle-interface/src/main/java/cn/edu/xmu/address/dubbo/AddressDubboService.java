package cn.edu.xmu.address.dubbo;

import java.util.List;

public interface AddressDubboService {
    public boolean isRegionIdValid(Long regionId);
    public List<Long> getPidsById(Long Id);
}
