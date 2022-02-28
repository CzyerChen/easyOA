package easyoa.leavemanager.service.impl;

import easyoa.core.domain.po.LoginWatchLog;
import easyoa.core.domain.po.OperationWatchLog;
import easyoa.core.repository.LoginWatchLogRepository;
import easyoa.core.repository.OperationWatchLogRepository;
import easyoa.core.service.LogService;
import easyoa.core.utils.HttpContextUtil;
import easyoa.core.utils.IPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by claire on 2019-06-21 - 11:30
 **/
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private OperationWatchLogRepository operationWatchLogRepository;
    @Autowired
    private LoginWatchLogRepository loginWatchLogRepository;

    @Override
    public void saveLoginLog(String username) {
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        String ip = IPUtil.getIpAddr(request);

        LoginWatchLog loginWatchLog = LoginWatchLog.builder()
                .ip(ip)
                .username(username)
                //.location(AddressUtil.getCityInfo(ip))
                .createTime(new Date())
                .build();

        loginWatchLogRepository.save(loginWatchLog);
    }

    @Override
    public void saveLoginLog(LoginWatchLog loginWatchLog) {

    }

    @Override
    public void saveOperationLog(OperationWatchLog operationWatchLog) {
        operationWatchLogRepository.save(operationWatchLog);
    }
}
