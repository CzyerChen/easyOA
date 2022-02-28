package easyoa.rulemanager.model;

/**
 * Created by claire on 2019-07-10 - 13:31
 **/
public class AppBeanFactory {

    public static <T> T getObject(String name){
        return (T)AppContextHolder.getApplicationContext().getBean(name);
    }

    public static <T> T getObject(Class<? extends T> clazz){
        return (T)AppContextHolder.getApplicationContext().getBean(clazz);
    }
}
