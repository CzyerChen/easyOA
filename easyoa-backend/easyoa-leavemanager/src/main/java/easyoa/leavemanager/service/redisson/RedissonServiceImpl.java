package easyoa.leavemanager.service.redisson;

import easyoa.leavemanager.domain.GlobalFlowDTO;
import easyoa.common.domain.dto.UserDTO;
import easyoa.common.domain.dto.UserLoginDTO;
import easyoa.core.domain.po.user.UserConfig;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by claire on 2019-06-21 - 18:46
 **/
@Service
public class RedissonServiceImpl<T> implements RedissonService {

    @Autowired
    private RedissonClient redissonClient;

    public RMap<String, String> getMap(String name) {
        return redissonClient.getMap(name);
    }

    @Override
    public RMap<String, UserDTO> getUserMap(String name) {
        return redissonClient.getMap(name);
    }

    @Override
    public RMap<String, Set<String>> getSet(String name) {
        return redissonClient.getMap(name);
    }

    @Override
    public Set<String> getSet(String mapName, String keyName) {
        RMap<String, Set<String>> map = redissonClient.getMap(mapName);
        return map.get(keyName);
    }


    public RMap<String, String> getMap(String name, MapOptions<String, String> options) {
        return redissonClient.getMap(name, options);
    }

    public RMapCache<String, Object> getMapCache(String name) {
        return redissonClient.getMapCache(name);
    }

    @Override
    public boolean existKey(String name,String key) {
        return redissonClient.getMap(name).containsKey(key);
    }

    public RMapCache<String, Long> getLimitRecording(String name) {
        return redissonClient.getMapCache(name);
    }

    @Override
    public RRateLimiter getRateLimiter(String name) {
        return redissonClient.getRateLimiter(name);
    }

    @Override
    public RMap<String, Long> getUserLastLoginTimeMap(String name) {
            return redissonClient.getMap(name);
    }

    @Override
    public RScoredSortedSet<UserLoginDTO> getActiveUserSet(String name) {
        return redissonClient.getScoredSortedSet(name);
    }

    @Override
    public RMapCache<String, String> getTokenMapCache(String name) {
        return redissonClient.getMapCache(name);
    }

    @Override
    public RMap<Long, UserConfig> getUserConfigMap(String name) {
        return redissonClient.getMap(name);
    }

    @Override
    public RList<GlobalFlowDTO> getFlowList(String name) {
        return redissonClient.getList(name);
    }

    @Override
    public RMapCache<String, String> getVertifyCodeMap(String name) {
        return redissonClient.getMapCache(name);
    }

    @Override
    public RMapCache<String, Integer> getMailRequestMap(String name) {
        return redissonClient.getMapCache(name);
    }

    @Override
    public <T> T getMapValues(String name, String key) {
        RMap<String, T> map = redissonClient.getMap(name);
        return map.get(key);
    }
}
