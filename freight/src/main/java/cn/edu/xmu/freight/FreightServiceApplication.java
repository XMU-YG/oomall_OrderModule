package cn.edu.xmu.freight;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.freight","cn.edu.xmu.ooad"})
@MapperScan("cn.edu.xmu.freight.mapper")
public class FreightServiceApplication {
    public static void main(String[] args) {
        System.out.println("111");
        SpringApplication.run(FreightServiceApplication.class, args);
    }
}
