package easyoa.leavemanager.runner.system;

import easyoa.common.domain.dto.UserDTO;
import easyoa.common.exception.BussinessException;
import easyoa.core.domain.po.user.UserConfig;
import easyoa.core.service.UserConfigService;
import easyoa.core.service.UserService;
import easyoa.leavemanager.service.CacheService;
import easyoa.leavemanager.service.GlobalFlowService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by claire on 2019-06-26 - 17:14
 **/
@Slf4j
@Component
public class ConfigInitialzer implements CommandLineRunner {
    @Autowired
    private UserConfigService userConfigService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserService userService;
    @Autowired
    private GlobalFlowService globalFlowService;

    @Override
    public void run(String... args) {
        try {
            cacheUserConfig();
            cacheUsers();
            clearTokencache();
            cacheGlobalFlow();
        }catch (Exception e){
            log.error("程序配置初始化失败，请检查ConfigInitialzer",e);
            throw  new BussinessException("程序配置初始化失败");
        }
    }

    private void cacheUserConfig(){
        RMap<Long, UserConfig> userConfigMap = cacheService.getUserConfigMap();
        if(!userConfigMap.isEmpty()){
            userConfigMap.clear();
        }

        List<UserConfig> userConfigs = userConfigService.getUserConfigs();
        if(userConfigs != null && userConfigs.size()>0){
            userConfigs.forEach( u ->{
                userConfigMap.put(u.getUserId(),u);
            });
        }
    }


    private void cacheUsers(){
        List<UserDTO> users = userService.getUsers();
        if(users != null && users.size()>0) {
            RMap<String, UserDTO> userMap = cacheService.getUserMap();
            if (!userMap.isEmpty()) {
                userMap.clear();
            }
            users.forEach(u ->{
                userMap.put(u.getUserName(),u);
            });
        }

    }

    private void clearTokencache(){
        RMapCache<String, String> tokenMapCache = cacheService.getTokenMapCache();
        if(!tokenMapCache.isEmpty()){
            tokenMapCache.clear();
        }
    }

    private void cacheGlobalFlow(){
       globalFlowService.cacheAllFlow();
    }
}
