package easyoa.leavemanager.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by claire on 2019-06-21 - 11:26
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperies {

    private boolean logOpen = true;
    private String uploadPath;
    private ShiroProperties shiro = new ShiroProperties();
}
