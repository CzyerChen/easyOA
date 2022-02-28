package easyoa.leavemanager.config.api;

/**
 * Created by claire on 2019-07-04 - 16:45
 **/
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


   /* @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }*/

    /*@Bean
    public Encoder multipartFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(new ObjectFactory<HttpMessageConverters>() {
            @Override
            public HttpMessageConverters getObject() throws BeansException {
                return new HttpMessageConverters(new RestTemplate().getMessageConverters());
            }
        }));
    }*/
/*    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder()
                .encoder(multipartFormEncoder())
                .decoder(new JacksonDecoder())
                .options(new Request.Options(1000, 5000))
                .retryer(new Retryer.Default(5000, 5000, 3));
    }

    @Bean
    public feign.Logger.Level multipartLoggerLevel() {
        return feign.Logger.Level.FULL;
    }*/



    /*@Bean
    @Primary
    @Scope("prototype")
    public Encoder multipartFormEncoder() {
        return new SpringFormEncoder();
    }
*/
/*
    @Bean
    public Encoder multipartFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(new ObjectFactory<HttpMessageConverters>() {
            @Override
            public HttpMessageConverters getObject() throws BeansException {
                return new HttpMessageConverters(new RestTemplate().getMessageConverters());
            }
        }));
    }*/

  /*  @Bean("feignFormEncoder")
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }

    @Bean("fileFeignBuilder")
    @Scope("prototype")
    @ConditionalOnBean(name = "feignFormEncoder")
    public Feign.Builder fileFeignBuilder() {
        return Feign.builder()
                .encoder(feignFormEncoder())//new JacksonEncoder()
                .decoder(new JacksonDecoder())
                .options(new Request.Options(1000, 5000))
                .retryer(new Retryer.Default(5000, 5000, 3));
    }




    @Bean
    public feign.Logger.Level multipartLoggerLevel() {
        return feign.Logger.Level.FULL;
    }

    @Bean("fileServerFallBack")
    public FileServerFallBack fsfb() {
        return new FileServerFallBack();
    }*/

}
