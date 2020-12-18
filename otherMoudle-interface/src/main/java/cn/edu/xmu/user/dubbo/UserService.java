package cn.edu.xmu.user.dubbo;

public interface UserService {
    public String findCustomerById(Long customerId);

    public boolean reduceRebate(Long userId, Long rebate);
}
