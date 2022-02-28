package easyoa.leavemanager.handler;

import com.google.common.reflect.Invokable;
import easyoa.leavemanager.domain.user.UserVacation;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Created by claire on 2019-10-05 - 16:44
 **/
@Slf4j
public class UserVacationInvokeHandler implements InvokeStrategy {
    @Override
    public Double getByColumn(String type,String leaveType,String methodName, Object obj) {
        UserVacation userVacation = (UserVacation) obj;
        try {
            Method method = UserVacation.class.getDeclaredMethod(methodName);
            Invokable<UserVacation, Object> invokable =
                    (Invokable<UserVacation, Object>) Invokable.from(method);
            Double invoke = (Double) invokable.invoke(userVacation);
            return invoke;
        }catch (Exception e ){
            log.error("方法调用失败,type：{}，method：{}",type,methodName);
        }
        return null;
    }

    @Override
    public void setByColumn(String type, String leaveType,String methodName,Double param, Object obj) {
        UserVacation userVacation = (UserVacation) obj;
        try {
            Method method = UserVacation.class.getDeclaredMethod(methodName,Double.class);
            Invokable<UserVacation, Object> invokable =
                    (Invokable<UserVacation, Object>) Invokable.from(method);
           invokable.invoke(userVacation,param);
        }catch (Exception e ){
            log.error("方法调用失败,type：{}，method：{}",type,methodName);
        }
    }
}
