package easyoa.leavemanager.web.controller;

import com.google.common.util.concurrent.AtomicLongMap;
import easyoa.common.constant.UserConstant;
import easyoa.common.domain.ApiResponse;
import easyoa.common.domain.dto.UserLoginDTO;
import easyoa.common.exception.BussinessException;
import easyoa.common.utils.CommonUtil;
import easyoa.common.utils.DateUtil;
import easyoa.common.utils.EncryptUtil;
import easyoa.core.domain.po.user.User;
import easyoa.core.service.LogService;
import easyoa.core.service.UserService;
import easyoa.leavemanager.annotation.Limit;
import easyoa.leavemanager.config.properties.AppProperies;
import easyoa.leavemanager.domain.jwt.JWTToken;
import easyoa.leavemanager.runner.system.MailServer;
import easyoa.leavemanager.service.CacheService;
import easyoa.leavemanager.utils.JWTUtil;
import easyoa.leavemanager.utils.ShiroMD5Util;
import easyoa.leavemanager.web.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by claire on 2019-06-24 - 11:30
 **/
@Validated
@RestController("/")
@Api(value = "登录相关接口",description = "登录相关接口")
public class LoginController extends AbstractController {

    public static final AtomicLongMap<String> countMap =AtomicLongMap.create();

    @Autowired
    private UserService userService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private AppProperies appProperies;
    @Autowired
    private LogService logService;
    @Autowired
    private MailServer mailServer;

    private static final String MAIL_TEXT_FOOTER="<br/> 系统登录链接：http://fiboapp.corp.com:8081/#/login";


    /**
     * 登录接口
     * @param username
     * @param password
     * @param request
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "登录接口")
    @PostMapping("/login")
    @Limit(key = "login", period = 60, count = 20, name = "登录接口", prefix = "limit")
    public ApiResponse login(@NotBlank(message = "{required}") String username,
                             @NotBlank(message = "{required}") String password, HttpServletRequest request) throws Exception {
        username = StringUtils.lowerCase(username);
        password = ShiroMD5Util.encrypt(username, password);

        final String errorMessage = "用户名或密码错误";
        User user = userService.getUser(username);

        if (user == null) {
            throw new BussinessException(errorMessage);
        }
        if (!StringUtils.equals(user.getPassword(), password)) {
            throw new BussinessException(errorMessage);
        }
        if (UserConstant.USER_STATUS_LOCK.equals(user.getStatus())) {
            throw new BussinessException("账号已被锁定,请联系管理员！");
        }
        // 保存登录记录
        logService.saveLoginLog(username);
//
        String token = CommonUtil.encryptToken(JWTUtil.sign(username, password));
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(appProperies.getShiro().getJwtTimeOut());
        String expireTimeStr = DateUtil.formatFullTime(expireTime);

        JWTToken jwtToken = new JWTToken(token, expireTimeStr);

        String userId = cacheService.saveUserTokenInfo(username, jwtToken, request);
        user.setId(userId);

        Map<String, Object> userInfo = userService.initUserInfoVO( user,jwtToken.getToken(),jwtToken.getExipreAt());
        // 更新用户登录时间,仅仅更新一个时间
        cacheService.saveLoginTime(username);

        return ApiResponse.success(userInfo);
    }

    /**
     * 登出用户
     * @param userId
     * @throws Exception
     */
    @ApiOperation(value = "登出接口")
    @GetMapping("/logout/{userId}")
    public void logout(@NotBlank(message = "{required}")@PathVariable String userId) throws Exception {
        this.kickout(userId);
    }


    /**
     * 重置密码
     * @param name
     */
    @ApiOperation(value ="重置密码接口")
    @GetMapping("/mail/{name}")
    public void mail(@NotBlank(message = "required")@PathVariable String name) throws UnsupportedEncodingException, MessagingException {

        RMapCache<String, Integer> requestMap = cacheService.getMailRequestMap();
        if(requestMap.containsKey(name)){
            Integer limit = requestMap.get(name);
            if( limit> 5){
                throw new BussinessException("尝试次数过多，请稍后重试");
            }else{
               limit++;
               requestMap.put(name,limit);
            }
        }else{
            requestMap.put(name,0,60,TimeUnit.SECONDS);
        }
        String vertifyCode = EncryptUtil.randomMailVertifyCode();
        RMapCache<String, String> codeMap = cacheService.getMailVertifyCodeMap();
        if(codeMap.containsKey(name)){
            codeMap.remove(name);
            codeMap.put(name,vertifyCode);
        }else{
            codeMap.put(name,vertifyCode,60, TimeUnit.SECONDS);
        }
        String content = "您需要重置账户的验证码内容是："+vertifyCode+", 验证码时效60s,请尽快认证! " +MAIL_TEXT_FOOTER;
        mailServer.sendSimpleMail(name,"系统-密码重置验证码",content);
    }


    /**
     * 剔除用户
     * @param userId
     * @throws Exception
     */
    @ApiOperation(value = "剔除接口")
    @DeleteMapping("kickout/{userId}")
    @RequiresPermissions("user:kickout")
    public void kickout(@NotBlank(message = "{required}") @PathVariable String userId) throws Exception {
        String username = userService.getUserNameById(Long.valueOf(userId));
        if(StringUtils.isNotBlank(username)) {
            UserLoginDTO userLoginDTO = null;
            String now = DateUtil.formatFullTime(LocalDateTime.now());
            Collection<UserLoginDTO> loginUsers = cacheService.getLoginUserListByRange(Double.valueOf(now), Double.MAX_VALUE);
            for(UserLoginDTO dto : loginUsers){
                if(StringUtils.equals(dto.getUsername(),username)){
                    userLoginDTO= dto;
                }
            }
            if(userLoginDTO != null){
               userService.removeActiveUser(userLoginDTO);
               userService.removeUserToken(userLoginDTO.getToken()+"."+userLoginDTO.getIp());
            }
        }
    }
}
