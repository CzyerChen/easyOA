package easyoa.leavemanager.service;

import easyoa.leavemanager.domain.GlobalFlowDTO;
import easyoa.leavemanager.domain.jwt.JWTToken;
import easyoa.common.domain.dto.UserDTO;
import easyoa.common.domain.dto.UserLoginDTO;
import easyoa.core.domain.po.user.UserConfig;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RScoredSortedSet;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Set;

/**
 * Created by claire on 2019-06-24 - 14:41
 **/
public interface CacheService {
    void saveLoginTime(String username);

    String saveUserTokenInfo(String username, JWTToken token, HttpServletRequest request);

    Collection<UserLoginDTO> getLoginUserListByRange(double min, double max);

    RMap<String, UserDTO> getUserMap();

    RMap<Long, UserConfig> getUserConfigMap();

    RMapCache<String, String> getTokenMapCache();

    RList<GlobalFlowDTO> getFlowList(Integer companyId);

    RMap<String, Set<String>> getRoleSet();

    RMap<String, Set<String>> getPermsSet();

    Set<String> getUserPermsSet(String userName);

    RScoredSortedSet<UserLoginDTO> getActiveUserSet();

    RMap<String,Long> getUserLastLoginTimeMap();

    RMapCache<String,String> getMailVertifyCodeMap();

    RMapCache<String, Integer> getMailRequestMap();

}
