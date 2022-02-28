package easyoa.core.service;

import easyoa.core.domain.po.LoginWatchLog;
import easyoa.core.domain.po.OperationWatchLog;

/**
 * Created by claire on 2019-06-21 - 11:29
 **/
public interface LogService {

    public void saveLoginLog(String username);

    public void saveLoginLog(LoginWatchLog loginWatchLog);

    public void saveOperationLog(OperationWatchLog operationWatchLog);
}
