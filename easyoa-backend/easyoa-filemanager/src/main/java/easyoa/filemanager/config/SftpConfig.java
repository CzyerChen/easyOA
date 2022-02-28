package easyoa.filemanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by claire on 2019-07-11 - 13:31
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "file")
public class SftpConfig {
    private String server;
    private int port;
    private String login;
    private String password;

}
