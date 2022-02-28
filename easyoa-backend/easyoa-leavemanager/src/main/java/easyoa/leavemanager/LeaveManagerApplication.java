package easyoa.leavemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by claire on 2019-06-20 - 13:49
 **/
@SpringBootApplication
@EnableJpaRepositories(basePackages = {"easyoa.leavemanager.repository","easyoa.core.repository"})
@ComponentScan(basePackages = {"easyoa.leavemanager","easyoa.common","easyoa.core"})
@EntityScan(basePackages = {"easyoa.leavemanager", "easyoa/common","easyoa.core"})
@EnableFeignClients
@ServletComponentScan
public class LeaveManagerApplication {

    public static void main(String[] args){
        SpringApplication.run(LeaveManagerApplication.class,args);
    }
}
