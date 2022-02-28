package easyoa.leavemanager.aspect;

import easyoa.core.domain.po.OperationWatchLog;
import easyoa.core.service.LogService;
import easyoa.core.utils.HttpContextUtil;
import easyoa.core.utils.IPUtil;
import easyoa.leavemanager.utils.JWTUtil;
import easyoa.leavemanager.annotation.Log;
import easyoa.leavemanager.config.properties.AppProperies;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by claire on 2019-06-21 - 11:03
 **/
@Component
@Aspect
@Slf4j
public class LogAspect {
    @Autowired
    private AppProperies appProperies;

    @Autowired
    private LogService logService;

    @Pointcut("@annotation(easyoa.leavemanager.annotation.Log)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        /*System.out.println("Before增强：模拟权限检查");
        System.out.println("Before增强：被织入增强处理的目标目标方法为：" + joinPoint.getSignature().getName());
        System.out.println("Before增强：目标方法的参数为：" + Arrays.toString(joinPoint.getArgs()));
        joinPoint.getArgs()[0] = "除了Around其他的都是是不可以修改目标方法的参数的";
        System.out.println("joinPoint.getArgs()[0]:"+joinPoint.getArgs()[0]);
        System.out.println("Before增强：目标方法的参数为：" + Arrays.toString(joinPoint.getArgs()));
        System.out.println("Before增强：被织入增强处理的目标对象为：" + joinPoint.getTarget());*/
    }

    @Around("pointcut()&& @annotation(logAnnotation)")
    public Object around(ProceedingJoinPoint point, Log logAnnotation) throws Throwable {
        Object result = null;
        long beginTime = System.currentTimeMillis();
        OperationWatchLog log = new OperationWatchLog();

        String params = null;
        Object[] args = point.getArgs();
        if (args != null && args.length > 0) {
            params = Arrays.toString(point.getArgs());
            log.setParams(params);
        }

        String method = point.getSignature().getName();
        if (StringUtils.isNotBlank(method)) {
            log.setMethod(method);
        }

        // 执行方法
        result = point.proceed();
        // 获取 request
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        // 设置 IP 地址
        String ip = IPUtil.getIpAddr(request);
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        if (appProperies.isLogOpen()) {
            // 保存日志
            String token = (String) SecurityUtils.getSubject().getPrincipal();
            String username = "";
            if (StringUtils.isNotBlank(token)) {
                username = JWTUtil.getUsername(token);
            }

            log.setIp(ip);
            log.setUsername(username);
            log.setTime(time);
            log.setCreateTime(new Date());
            log.setOperation(logAnnotation.value());
            logService.saveOperationLog(log);
        }
        return result;
    }

    @AfterReturning(pointcut = "execution(* easyoa.leavemanager.web.controller.*.*(..))", returning = "rvt")
    public void afterReturning(JoinPoint joinPoint, Object rvt) {
        /*System.out.println("AfterReturning增强：获取目标方法的返回值：" + rvt);
        System.out.println("AfterReturning增强：模拟日志功能");
        System.out.println("AfterReturning增强：获织入增强的目标方法为：" + joinPoint.getSignature().getName());
        System.out.println("AfterReturning增强：目标方法的参数为：" + Arrays.toString(joinPoint.getArgs()));
        System.out.println("AfterReturning增强：被织入增强处理的目标对象为：" + joinPoint.getTarget());*/
    }
}
