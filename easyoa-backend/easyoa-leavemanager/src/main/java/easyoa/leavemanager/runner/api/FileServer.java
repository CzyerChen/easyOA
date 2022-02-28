package easyoa.leavemanager.runner.api;

import feign.*;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.jackson.JacksonDecoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by claire on 2019-07-15 - 18:21
 **/
@FeignClient(
        value = "file-server",
        fallback = FileServerFallBack.class,
        url = "http://file:7070",
        configuration = FileServer.FeignConfig.class
)
public interface FileServer {

    @RequestMapping(value = "/file/do", method = RequestMethod.GET)
    Long uploadFile(@RequestParam("name") String name, @RequestParam("userId") Long userId);

    @RequestMapping(value = "/file/download", method = RequestMethod.GET)
    String downloadFile(@RequestParam("fileId") String fileId);

    @RequestMapping(value = "/file/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void handleFileUpload(@RequestPart(value = "file") MultipartFile file, @RequestParam(value = "userId") String userId);


 /*   @RequestMapping(method = RequestMethod.POST,value = "/file/upload",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
           consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void uploadFile2Local(@RequestPart("apply-file")MultipartFile multipartFile, @RequestParam("userId")String userId);*/


    class FeignConfig {

        @Bean("feignFormEncoder")
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
        }

    }

}
