package easyoa.leavemanager.service.impl;

import easyoa.leavemanager.config.properties.AppProperies;
import easyoa.leavemanager.domain.GlobalFlowDTO;
import easyoa.leavemanager.domain.jwt.JWTToken;
import easyoa.leavemanager.service.CacheService;
import easyoa.common.constant.CacheConstant;
import easyoa.common.domain.dto.UserDTO;
import easyoa.common.domain.dto.UserLoginDTO;
import easyoa.core.domain.po.user.UserConfig;
import easyoa.core.utils.IPUtil;
import easyoa.leavemanager.service.redisson.RedissonService;
import jodd.util.StringPool;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by claire on 2019-06-24 - 14:42
 **/
@Service
public class CacheServiceImpl implements CacheService {
    @Autowired
    private RedissonService redissonService;
    @Autowired
    private AppProperies appProperies;

    @Override
    public void saveLoginTime(String username) {

        RMap<String, Long> loginTimeMap = redissonService.getUserLastLoginTimeMap(CacheConstant.USER_LAST_LOGIN_CACHE_NAME);
        if (loginTimeMap.containsKey(username)) {
            loginTimeMap.replace(username, System.currentTimeMillis());
        } else {
            loginTimeMap.put(username, System.currentTimeMillis());
        }
    }

    @Override
    public String saveUserTokenInfo(String username, JWTToken token, HttpServletRequest request) {
        //保存在线用户信息
        String ipAddr = IPUtil.getIpAddr(request);
        UserLoginDTO loginDTO = UserLoginDTO.builder()
                .ip(ipAddr)
                .token(token.getToken())
                .username(username)
                //.loginAddress(AddressUtil.getCityInfo(ipAddr))
                .build();
        RScoredSortedSet<UserLoginDTO> activeUserSet = redissonService.getActiveUserSet(CacheConstant.USER_LOGIN_DETAIL_CACHE_NAME);
        //LocalDateTime dt = LocalDateTime.parse(token.getExipreAt(), DateTimeFormatter.ofPattern(DateUtil.BASE_PATTERN));
        activeUserSet.add(Double.valueOf(token.getExipreAt()), loginDTO);

        //保存token信息
        // redis 中存储这个加密 token，key = 前缀 + 加密 token + .ip
        RMapCache<String, String> tokenMapCache = redissonService.getTokenMapCache(CacheConstant.USER_LOGIN_TOKEN_CACHE_NAME);
        tokenMapCache.put(token.getToken() + StringPool.DOT + ipAddr, token.getToken(), appProperies.getShiro().getJwtTimeOut(), TimeUnit.SECONDS);
        return loginDTO.getId();
    }

    @Override
    public Collection<UserLoginDTO> getLoginUserListByRange(double min, double max) {
        RScoredSortedSet<UserLoginDTO> activeUserSet = redissonService.getActiveUserSet(CacheConstant.USER_LOGIN_DETAIL_CACHE_NAME);
        return activeUserSet.valueRange(min, true, max, true);
    }

    @Override
    public RMap<String, UserDTO> getUserMap() {
        return redissonService.getUserMap(CacheConstant.USER_CACHE_NAME);
    }

    @Override
    public RMap<Long, UserConfig> getUserConfigMap() {
        return redissonService.getUserConfigMap(CacheConstant.USER_CONFIG_CACHE_NAME);
    }

    @Override
    public RMapCache<String, String> getTokenMapCache() {
        return redissonService.getTokenMapCache(CacheConstant.USER_LOGIN_TOKEN_CACHE_NAME);
    }

    @Override
    public RList<GlobalFlowDTO> getFlowList(Integer companyId) {
        return redissonService.getFlowList(CacheConstant.GLOBAL_FLOE_CACHE_NAME+companyId);
    }

    @Override
    public RMap<String, Set<String>> getRoleSet() {
        return redissonService.getSet(CacheConstant.USER_ROLE_CACHE_NAME);
    }

    @Override
    public RMap<String, Set<String>> getPermsSet() {
        return redissonService.getSet(CacheConstant.USER_PERMS_CACHE_NAME);
    }

    @Override
    public Set<String> getUserPermsSet(String userName) {
        return redissonService.getSet(CacheConstant.USER_PERMS_CACHE_NAME,userName);
    }

    @Override
    public RScoredSortedSet<UserLoginDTO> getActiveUserSet() {
        return redissonService.getActiveUserSet(CacheConstant.USER_LOGIN_DETAIL_CACHE_NAME);
    }

    @Override
    public RMap<String, Long> getUserLastLoginTimeMap() {
        return redissonService.getUserLastLoginTimeMap(CacheConstant.USER_LAST_LOGIN_CACHE_NAME);
    }

    @Override
    public RMapCache<String, String> getMailVertifyCodeMap() {
        return redissonService.getVertifyCodeMap(CacheConstant.MAIL_VERTIFY_CODE_CACHE_NAME);
    }

    @Override
    public RMapCache<String, Integer> getMailRequestMap() {
        return redissonService.getMailRequestMap(CacheConstant.MAIL_VERTIFY_COUNT_CACHE_NAME);
    }

}
