package easyoa.leavemanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by claire on 2019-06-21 - 10:54
 **/
@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Value("${swagger.enable}")
    private boolean enableSwagger;

    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enableSwagger)
                .select()
                .apis(RequestHandlerSelectors.basePackage("easyoa.leavemanager.web.controller"))
                .paths(PathSelectors.any())
                .build();

        /**
         *  ParameterBuilder tokenPar = new ParameterBuilder();
         *         List<Parameter> pars = new ArrayList<Parameter>();
         *         tokenPar.name("token")
         *                 .defaultValue("user-eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbjAifQ.EzOtp4tB1dD7xTGWIc5Dlceoi7undj9ikhDdkuz23N_te3iLoE61nqSd-X-9hmC_ERIdKMXu62ZHbuV4vqWzhQ")
         *                 .description("令牌")
         *                 .modelRef(new ModelRef("string")).parameterType("header").required(false).build();
         *         pars.add(tokenPar.build());
         *         return new Docket(DocumentationType.SWAGGER_2)
         *                 .select()
         *                 .apis(RequestHandlerSelectors.basePackage("com.arthur.demo.controller"))
         *                 .paths(PathSelectors.any())
         *                 .build()
         *                 .globalOperationParameters(pars)
         *                 .apiInfo(apiInfo());
         */

    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("lms 管理系统-休假部分 接口文档")
                .description("easyoa 管理系统 接口文档列表")
                .termsOfServiceUrl("http://localhost:8080")
                .contact("ClaireChen")
                .version("1.0.0")
                .build();
    }
}
