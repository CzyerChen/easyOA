package easyoa.leavemanager.handler;

/**
 * Created by claire on 2019-10-05 - 17:03
 **/
public class InvokeStrategyContext {
    private InvokeStrategy strategy;

    public Double getByColumn(String type, String leaveType, String methodName,Object obj) {
        strategy = InvokeStrategyFactory.getInstance().creator(type);
        return strategy.getByColumn(type, leaveType, methodName,obj);
    }

    public void setByColumn(String type, String leaveType,String methodName, Double param, Object obj) {
        strategy = InvokeStrategyFactory.getInstance().creator(type);
        strategy.setByColumn(type, leaveType,methodName, param, obj);
    }

    public InvokeStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(InvokeStrategy strategy) {
        this.strategy = strategy;
    }
}
