package cn.edu.xmu.oomallgateway.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import cn.edu.xmu.privilegeservice.client.IGatewayService;
import javax.annotation.PostConstruct;

/**
 * 网关工具类
 * @author  wwc
 * @Date :   Created in  2020/12/6 15:45
 * @Modified By : Jintai Wang
 */
@Component
@Slf4j // 打印日志直接调用log.打印日志
public class GatewayUtil {

    @Value("${privilegegateway.jwtExpire:3600}")
    private static Integer jwtExpireTime = 3600;

    @Value("${privilegegateway.refreshJwtTime:60}")
    private static Integer refreshJwtTime = 60;


    public static Integer getJwtExpireTime() {
        return jwtExpireTime;
    }

    public static Integer getRefreshJwtTime() {
        return refreshJwtTime;
    }

    private final  String urlKeyName = "User";

}
