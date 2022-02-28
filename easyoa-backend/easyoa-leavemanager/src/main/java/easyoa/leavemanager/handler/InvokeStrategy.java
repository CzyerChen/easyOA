package easyoa.leavemanager.handler;

/**
 * Created by claire on 2019-10-05 - 16:42
 **/
public interface InvokeStrategy {
    Double getByColumn(String type,String leaveType,String methodName,Object obj);

    void setByColumn(String type,String leaveType,String methodName,Double param,Object obj);
}
