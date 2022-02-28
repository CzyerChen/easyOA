package easyoa.rulemanager.model;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Created by claire on 2019-07-10 - 13:28
 **/
@Service
public class AppContextHolder implements ApplicationContextAware {
    private static volatile ApplicationContext applicationContext ;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
         AppContextHolder.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext(){
        return  applicationContext;
    }
}
