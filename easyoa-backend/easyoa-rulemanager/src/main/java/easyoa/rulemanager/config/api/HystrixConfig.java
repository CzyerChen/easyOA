package easyoa.rulemanager.config.api;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

/**
 * Created by claire on 2019-07-04 - 16:27
 **/
@Configuration
public class HystrixConfig {


    /**
     * A {@link ServletContextInitializer} to register {@link Servlet}s in a Servlet 3.0+ container.
     */
    @Bean
    public ServletRegistrationBean hystrixMetricsStreamServlet() {
        return new ServletRegistrationBean(new HystrixMetricsStreamServlet(), "/hystrix.stream");
    }

    /**
           * AspectJ aspect to process methods which annotated with {@link HystrixCommand} annotation.
           *
          * {@link HystrixCommand} annotation used to specify some methods which should be processes as hystrix commands.
           */
    @Bean
    public HystrixCommandAspect hystrixCommandAspect() {
        return new HystrixCommandAspect();
    }

}
