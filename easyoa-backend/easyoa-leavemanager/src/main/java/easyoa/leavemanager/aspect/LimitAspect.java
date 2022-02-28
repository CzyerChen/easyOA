package easyoa.leavemanager.aspect;

import com.google.common.collect.ImmutableList;
import easyoa.common.constant.LimitTypeEnum;
import easyoa.core.exception.LimitAccessException;
import easyoa.core.utils.IPUtil;
import easyoa.leavemanager.annotation.Limit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * Created by claire on 2019-06-24 - 11:39
 **/
@Aspect
@Component
@Slf4j
public class LimitAspect {
    @Autowired
    private LimitRateRepo limitRateRepo;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Pointcut("@annotation(easyoa.leavemanager.annotation.Limit)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Limit limitAnnotation = method.getAnnotation(Limit.class);
        LimitTypeEnum limitTypeEnum = limitAnnotation.limitType();
        //接口名称
        String name = limitAnnotation.name();
        String key;
        String ip = IPUtil.getIpAddr(request);
        int limitPeriod = limitAnnotation.period();
        int limitCount = limitAnnotation.count();
        //限流类型
        switch (limitTypeEnum) {
            case IP:
                key = ip;
                break;
            case CUSTOMER:
                key = limitAnnotation.key();
                break;
            default:
                key = StringUtils.upperCase(method.getName());
        }
        String cacheName = limitAnnotation.prefix() + "_" + key;
        ImmutableList<String> keys = ImmutableList.of(StringUtils.join(limitAnnotation.prefix() + "_", key, ip));

        //仅限于专业版才有的功能
       /* RRateLimiter limiter = limitRateRepo.getLimiter(cacheName);
        boolean tryAcquire = limiter.tryAcquire(1, TimeUnit.SECONDS);
        if(tryAcquire){
            log.info("IP:{} 第  次访问key为 {}，描述为 [{}] 的接口", ip, keys, name);
            return point.proceed();
        }else {
            throw new LimitAccessException("接口访问超出频率限制");
        }*/
       Number count = 0;//executeLuaScript(keys,limitCount,limitPeriod);
        log.info("IP:{} 第 {} 次访问key为 {}，描述为 [{}] 的接口", ip, count, keys, name);
        if (count != null && count.intValue() <= limitCount) {
            return point.proceed();
        } else {
            throw new LimitAccessException("接口访问超出频率限制");
        }
    }


    /**
     * 脚本性能比较高
     * @param keys
     * @param limitCount
     * @param limitPeriod
     * @return
     */
    private Number executeLuaScript(List<String> keys, int limitCount, int limitPeriod) {

        String script =  "local c" +
                "\nc = redis.call('get',KEYS[1])" +
                "\nif c and tonumber(c) > tonumber(ARGV[1]) then" +
                "\nreturn c;" +
                "\nend" +
                "\nc = redis.call('incr',KEYS[1])" +
                "\nif tonumber(c) == 1 then" +
                "\nredis.call('expire',KEYS[1],ARGV[2])" +
                "\nend" +
                "\nreturn c;";

        RedisScript<Number> redisScript = new DefaultRedisScript<>(script, Number.class);
        return redisTemplate.execute(redisScript, keys, limitCount, limitPeriod);
    }
}
