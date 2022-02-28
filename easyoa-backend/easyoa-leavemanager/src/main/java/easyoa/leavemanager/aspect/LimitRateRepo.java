package easyoa.leavemanager.aspect;

import easyoa.leavemanager.service.redisson.RedissonService;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by claire on 2019-06-24 - 18:38
 **/
@Service
public class LimitRateRepo {
    @Autowired
    private RedissonService redissonService;

    public ConcurrentHashMap<String, RRateLimiter> limiterMap = new ConcurrentHashMap<>();

    public void addLimiter(String cacheName, RRateLimiter limiter, long time, long limitCount, RateIntervalUnit intervalUnit){
        RRateLimiter rateLimiter = redissonService.getRateLimiter(cacheName);
        rateLimiter.trySetRate(RateType.OVERALL,time,limitCount,intervalUnit);
        limiterMap.putIfAbsent(cacheName,limiter);
    }

    public RRateLimiter getLimiter(String cacheName){
        return limiterMap.get(cacheName);
    }
}
