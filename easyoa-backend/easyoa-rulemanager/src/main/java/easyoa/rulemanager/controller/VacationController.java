package easyoa.rulemanager.controller;


import easyoa.common.domain.vo.VacationCalSearch;
import easyoa.common.exception.BussinessException;
import easyoa.common.utils.DateUtil;
import easyoa.rulemanager.domain.User;
import easyoa.rulemanager.domain.UserDetail;
import easyoa.rulemanager.domain.UserVacation;
import easyoa.rulemanager.domain.UserVacationCal;
import easyoa.rulemanager.service.UserService;
import easyoa.rulemanager.service.UserVacationCalService;
import easyoa.rulemanager.service.UserVacationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by claire on 2019-07-25 - 17:21
 **/
@Slf4j
@RestController
@RequestMapping("/vacation")
public class VacationController extends AbstractController {

    @Autowired
    private UserVacationService userVacationService;
    @Autowired
    private UserVacationCalService userVacationCalService;
    @Autowired
    private UserService userService;


    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public void saveUserVacation(@NotBlank @RequestBody String userId) {
        userVacationService.saveDefaultVacation(Long.valueOf(userId));
    }

    @RequestMapping(value = "/user/cal", method = RequestMethod.POST)
    public void saveUserVacationCal(@NotBlank @RequestBody String userId) {
        UserVacation userVacation = userVacationService.findUserVacation(Long.valueOf(userId),LocalDate.now().getYear());
        if(userVacation != null) {
            userVacationCalService.saveDefaultVacationCal(userVacation);
        }
    }

    @RequestMapping(value = "/cal", method = RequestMethod.POST)
    public Boolean updateUserVacationCal(@RequestBody VacationCalSearch search) {
        if (StringUtils.isNotBlank(search.getUserCode())) {
            UserDetail userDetail = userService.getUserDetailById(search.getUserCode());
            try {
                if (StringUtils.isNotBlank(search.getMonth())) {
                    LocalDate date = null;
                    try {
                        date = DateUtil.parseLocalDateWithAlias(search.getMonth() + "-01");

                    }catch (Exception e){
                        log.error("日期解析异常",e);
                        throw new BussinessException("日期解析错误");
                    }
                    if(date!= null) {
                        userVacationCalService.calForUser(userDetail, date);
                    }
            } else {
                for (int i = 1; i <= LocalDate.now().getMonthValue(); i++) {
                    userVacationCalService.calForUser(userDetail, LocalDate.of(LocalDate.now().getYear(),i,1));
                }
            }
            }catch (Exception e){
                log.error("计算月度报表出现异常",e);
                return false;
            }
        }
        return true;
    }

    @GetMapping("/init")
    public String initVacationCal(){
        try {
            List<User> activeUsers = userService.findAll();
            if (activeUsers != null && activeUsers.size() != 0) {
                activeUsers.forEach(a -> {
                    UserVacationCal cal = userVacationCalService.getUserVacationCalByUserCode(a.getUserCode(), LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1));
                    if (cal == null) {
                        UserVacation userVacation = userVacationService.findUserVacation(a.getUserId(),LocalDate.now().getYear());
                        userVacationCalService.saveDefaultVacationCal(userVacation);
                    }
                });
            }
            return "success";
        }catch (Exception e){
            log.error("初始化数据异常",e);
            return "error";
        }
    }

}
