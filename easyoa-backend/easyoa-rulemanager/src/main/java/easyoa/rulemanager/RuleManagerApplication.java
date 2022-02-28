package easyoa.rulemanager;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by claire on 2019-06-20 - 18:22
 **/
@EnableFeignClients
@EnableHystrix
@SpringBootApplication
@ComponentScan(basePackages = {"easyoa.rulemanager","easyoa.common"})
@EntityScan(basePackages = {"easyoa.rulemanager", "easyoa/common"})
@EnableScheduling
public class RuleManagerApplication {

    public static void main(String[] args){
        SpringApplication.run(RuleManagerApplication.class,args);
    }
}
