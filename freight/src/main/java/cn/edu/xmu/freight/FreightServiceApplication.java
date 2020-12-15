package cn.edu.xmu.freight;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableDubbo
@EnableDiscoveryClient
@EnableSwagger2
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.freight","cn.edu.xmu.ooad"})
@MapperScan("cn.edu.xmu.freight.mapper")
public class FreightServiceApplication {
    public static void main(String[] args) {
        System.out.println("111");
        SpringApplication.run(FreightServiceApplication.class, args);
    }
}
