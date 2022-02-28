package easyoa.leavemanager.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by claire on 2019-10-05 - 16:59
 **/
public class InvokeStrategyFactory {
    private static InvokeStrategyFactory factory = new InvokeStrategyFactory();
    private InvokeStrategyFactory(){
    }
    private static Map<String,InvokeStrategy> strategyMap = new HashMap<>();
    static{
        strategyMap.put("UserVacation", new UserVacationInvokeHandler());
        strategyMap.put("UserVacationCal", new UserVacationCalInvokeHandler());
    }
    public InvokeStrategy creator(String type){
        return strategyMap.get(type);
    }
    public static InvokeStrategyFactory getInstance(){
        return factory;
    }

}
