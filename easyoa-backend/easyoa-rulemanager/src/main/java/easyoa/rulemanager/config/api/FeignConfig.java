package easyoa.rulemanager.config.api;

import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

import easyoa.rulemanager.calendar.CalendarServerFallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by claire on 2019-07-04 - 16:45
 **/
@Configuration
public class FeignConfig {

   /* @Bean
    public CalendarServer calendarServer(){
        return Feign.builder()
                .options(new Request.Options(1000, 3500))
                .retryer(new Retryer.Default(5000, 5000, 3))
                .target(CalendarServer.class, "http://v.juhe.cn");
    }*/

    //使用feign的注解方式
    /*@Bean
    public Contract useFeignAnnotations() {
        return new Contract.Default();
    }*/

    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .options(new Request.Options(1000, 3500))
                .retryer(new Retryer.Default(5000, 5000, 3));
    }

    //实例化fallback
    @Bean
    public CalendarServerFallback fb() {
        return new CalendarServerFallback();
    }
}
