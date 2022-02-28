package easyoa.leavemanager.service.redisson;

import easyoa.leavemanager.domain.GlobalFlowDTO;
import easyoa.common.domain.dto.UserDTO;
import easyoa.common.domain.dto.UserLoginDTO;
import easyoa.core.domain.po.user.UserConfig;
import org.redisson.api.*;

import java.util.Set;

/**
 * Created by claire on 2019-06-21 - 18:36
 **/
public interface RedissonService {

    //public RMap<String,String> getMap(String name);

    RMap<String, UserDTO> getUserMap(String name);

    RMap<String, Set<String>> getSet(String name);

    Set<String> getSet(String mapName,String keyName);

    <T> T getMapValues(String name, String key);

    //public RMap<String,String> getMap(String name, MapOptions<String,String> options);

    //public RMapCache<String,Object> getMapCache(String name);

    boolean existKey(String name, String key);

    //public RMapCache<String, Long> getLimitRecording(String name);

    RRateLimiter getRateLimiter(String name);

    RMap<String, Long> getUserLastLoginTimeMap(String name);

    RScoredSortedSet<UserLoginDTO> getActiveUserSet(String name);

    RMapCache<String, String> getTokenMapCache(String name);

    RMap<Long, UserConfig> getUserConfigMap(String name);

    RList<GlobalFlowDTO> getFlowList(String name);

    RMapCache<String,String> getVertifyCodeMap(String name);

    RMapCache<String,Integer> getMailRequestMap(String name);

}
