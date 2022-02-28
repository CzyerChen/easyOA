package easyoa.filemanager;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by claire on 2019-06-21 - 09:26
 **/
@SpringBootApplication
@EnableJpaRepositories(basePackages = {"easyoa.common", "easyoa.filemanager"})
@EntityScan(basePackages = {"easyoa/common","easyoa.filemanager"})
@ComponentScan(basePackages = {"easyoa.common", "easyoa.filemanager"})
public class FileManagerApplication {

    public static  void main(String[] args){
        SpringApplication.run(FileManagerApplication.class,args);
    }
}
