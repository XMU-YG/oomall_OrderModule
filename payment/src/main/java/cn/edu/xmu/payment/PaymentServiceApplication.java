package cn.edu.xmu.payment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.payment","cn.edu.xmu.ooad"})
@MapperScan("cn.edu.xmu.payment.mapper")
public class PaymentServiceApplication {


        public static void main(String[] args) {
            System.out.println("111");
            SpringApplication.run(PaymentServiceApplication.class, args);
        }
    }

