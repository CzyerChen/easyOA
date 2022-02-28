package easyoa.leavemanager.service.impl;

import easyoa.leavemanager.domain.user.UserPosition;
import easyoa.leavemanager.service.UserPositionService;
import easyoa.leavemanager.repository.user.UserPositionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-06-26 - 22:23
 **/
@Slf4j
@Service("userPositionService")
public class UserPositionServiceImpl implements UserPositionService {
    @Autowired
    private UserPositionRepository userPositionRepository;

    @Override
    public UserPosition findByUserCode(String userCode) {
        return userPositionRepository.findByUserCode(userCode);
    }

    @Override
    public void saveUserPostionInfo(Map<String, String> positionInfo) {
        List<UserPosition> list = new ArrayList<>();
       positionInfo.forEach((k,v) ->{
           list.add(UserPosition.builder().name(v).userCode(k).description(v).build());
       });

       userPositionRepository.saveAll(list);
    }
}
