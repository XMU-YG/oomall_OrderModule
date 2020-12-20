package cn.edu.xmu.oomallgateway.localfilter;

import cn.edu.xmu.oomallgateway.util.GatewayUtil;
import cn.edu.xmu.oomallgateway.util.JwtHelper;
import cn.edu.xmu.privilegeservice.client.IGatewayService;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;


/**
 * 权限过滤器
 * @Author :   Jintai Wang
 * @Date :   Created in  2020/12/6 14:51
 * @Modified By :
 */

public class AuthFilter implements GatewayFilter, Ordered {

    private  static  final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    private String tokenName;

    public AuthFilter(Config config){
        this.tokenName = config.getTokenName();
    }

    @Value("${oomallgateway.jwtExpire:3600}")
    private Integer jwtExpireTime = 3600;

    /**
     * 权限过滤器
     * 1、 检查JWT是否合法，以及是否过期，如果过期则需要在在response的头里换发新JWT，如果不过期将旧的JWT在response的头中返回
     * Created By Jintai Wang in 2020/12/6 15:14
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 获取请求参数
        String token = request.getHeaders().getFirst(tokenName);
        HttpMethod method = request.getMethod();

        // 判断token是否为空
        logger.debug("filter:token = " + token);
        if (StringUtil.isNullOrEmpty(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.writeWith(Mono.empty());
        }

        // 判断jwt是否合法
        JwtHelper.UserAndDepart userAndDepart = new JwtHelper().verifyTokenAndGetClaims(token);
        if (userAndDepart == null){
            // token不合法
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.writeWith(Mono.empty());
        } else {
            // token合法
            Long userId = userAndDepart.getUserId();
            Long departId = userAndDepart.getDepartId();
            Date expireTime = userAndDepart.getExpTime();
            String key = "up_" + userId;
            String jwt = token;
            Long sec = expireTime.getTime() - System.currentTimeMillis();
            if( sec <GatewayUtil.getRefreshJwtTime() * 1000){
                // 若快要过期了换发新的token
                JwtHelper jwtHelper = new JwtHelper();
                jwt = jwtHelper.createToken(userId, departId, GatewayUtil.getJwtExpireTime());
                logger.debug("重新换发token:" + jwt);
            }
            response.getHeaders().set(tokenName,jwt);

            // 不校验权限，直接返回
            return chain.filter((exchange));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public static class Config {
        private String tokenName;

        public Config(){

        }

        public String getTokenName(){ return tokenName; }

        public void setTokenName(String tokenName) {
            this.tokenName = tokenName;
        }

    }

}
