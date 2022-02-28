package easyoa.leavemanager.matcher;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by claire on 2019-06-21 - 14:37
 **/
@Data
@Slf4j
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {


    private Cache<String, AtomicInteger> retryCount;
    private int retryMax ;

    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager){
        retryCount = cacheManager.getCache("login_retry_count");
    }


    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String)token.getPrincipal();

        AtomicInteger count = retryCount.get(username);
        if(count == null){
            retryCount.put(username,new AtomicInteger(0));
        }
        if(count.incrementAndGet() >retryMax){
            //超出次数，可以抛出异常
        }

        boolean match = super.doCredentialsMatch(token, info);
        if(match){
            retryCount.remove(username);
        }
        return  match;
    }
}
