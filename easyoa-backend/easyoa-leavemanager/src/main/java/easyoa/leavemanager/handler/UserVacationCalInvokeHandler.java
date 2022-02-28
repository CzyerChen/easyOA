package easyoa.leavemanager.handler;

import com.google.common.reflect.Invokable;
import easyoa.leavemanager.domain.user.UserVacationCal;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Created by claire on 2019-10-05 - 16:46
 **/
@Slf4j
public class UserVacationCalInvokeHandler implements InvokeStrategy {
    @Override
    public Double getByColumn(String type,String leaveType,String methodName,Object obj) {
        UserVacationCal userVacationCal = (UserVacationCal) obj;
        try {
            Method method = UserVacationCal.class.getDeclaredMethod(methodName);
            Invokable<UserVacationCal, Object> invokable =
                    (Invokable<UserVacationCal, Object>) Invokable.from(method);
            Double invoke = (Double) invokable.invoke(userVacationCal);
            return invoke;
        }catch (Exception e ){
            log.error("方法调用失败,type：{}，method：{}",type,methodName);
        }
        return null;
    }

    @Override
    public void setByColumn(String type, String leaveType,String methodName,Double param, Object obj) {
        UserVacationCal userVacationCal = (UserVacationCal) obj;
        try {
            Method method = UserVacationCal.class.getDeclaredMethod(methodName,Double.class);
            Invokable<UserVacationCal, Object> invokable =
                    (Invokable<UserVacationCal, Object>) Invokable.from(method);
            invokable.invoke(userVacationCal,param);
        }catch (Exception e ){
            log.error("方法调用失败,type：{}，method：{}",type,methodName);
        }
    }
}
