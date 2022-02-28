package easyoa.leavemanager.config.api;

/**
 * Created by claire on 2019-07-31 - 09:11
 **/
public class RuleFeignConfig {
   /* @Bean("ruleFeignBuilder")
    @Scope("prototype")
    public Feign.Builder ruleFeignBuilder() {
        return Feign.builder()
                .encoder(new JacksonEncoder())//
                .decoder(new JacksonDecoder())
                .options(new Request.Options(1000, 5000))
                .retryer(new Retryer.Default(5000, 5000, 3));
    }

    //实例化fallback
    @Bean("ruleServerFallBack")
    public RuleServerFallBack rsfb() {
        return new RuleServerFallBack();
    }*/
}
