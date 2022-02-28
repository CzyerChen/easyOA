package easyoa.leavemanager.service;

import easyoa.leavemanager.domain.user.UserPosition;

import java.util.Map;

/**
 * Created by claire on 2019-06-26 - 22:22
 **/
public interface UserPositionService {
    UserPosition findByUserCode(String userCode);

    void saveUserPostionInfo(Map<String,String> positionInfo);
}
