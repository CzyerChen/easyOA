package easyoa.rulemanager.task;

import easyoa.rulemanager.domain.*;
import easyoa.rulemanager.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-07-17 - 09:30
 **/
@Slf4j
@Component
public class DataCheckTask {
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private UserVacationService userVacationService;
    @Autowired
    private UserVacationCalService userVacationCalService;
    @Autowired
    private LeaveRulesService leaveRulesService;

    /**
     * 特殊假期由于原始数据的缺失，暂时做不了判断，原始数据也只能做带薪病假和年假的判断
     * 计算周申请，月申请，核算总假期数，用于首页展示
     */
    //@Scheduled(cron = "0 */1 * * * ?")
    @Scheduled(cron = "0 0 0 * * ?")
    public void refreshPersonalVacationData() {
        log.info("每五分钟，计算月，周，剩余假期数统计");
        List<User> activeUsers = userService.findAll();
        List<Long> ids = activeUsers.stream().map(User::getUserId).distinct().collect(Collectors.toList());
        ids.forEach(id -> {
            UserVacation userVacation = userVacationService.findUserVacation(id, LocalDate.now().getYear());

            if (userVacation == null) {
                userVacation = userVacationService.saveDefaultVacation(id);
            }

            Integer count1 = applicationService.findWeeklyEffectiveApplicationsCount(id);
            Integer count2 = applicationService.findMonthlyEffectiveApplicationsCount(id);
            Double count3 = userVacation.getAnnualLeave()
                    + userVacation.getCasualLeave()
                    + userVacation.getFuneralLeave()
                    + userVacation.getSickLeave()
                    + userVacation.getMarriageLeave()
                    + userVacation.getMaternityLeave()
                    + userVacation.getPaternityLeave()
                    + userVacation.getSickLeaveNormal();
            userVacation.setWeeklyApply(count1);
            userVacation.setMonthlyApply(count2);
            userVacation.setTotal(count3);

            userVacationService.saveUserVacation(userVacation);
        });
    }

    //每月初初始化默认数据，每月1号凌晨1点
    @Scheduled(cron = "0 0 1 1 * ?")
    public void calForUserVacationReportInitial() {
        log.info("月初初始化用户报表模板");
        List<User> activeUsers = userService.findAll();
        if (activeUsers != null && activeUsers.size() != 0) {
            activeUsers.forEach(a -> {
                UserVacationCal cal = userVacationCalService.getUserVacationCalByUserCode(a.getUserCode(), LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1));
                if (cal == null) {
                    UserVacation userVacation = userVacationService.findUserVacation(a.getUserId(), LocalDate.now().getYear());
                    userVacationCalService.saveDefaultVacationCal(userVacation);
                }
            });
        }
    }

    //月末出报表内容
    @Scheduled(cron = "0 0 */1 * * ?")// 0 0 2,9,13,19,21 * * ?
    public void calForUserVacationReport() {
        log.info("月末进行报表数据更新");
        List<User> activeUsers = userService.findAll();
        if (activeUsers != null && activeUsers.size() != 0) {
            activeUsers.forEach(a -> {
                try {
                    UserDetail userDetail = userService.getUserDetailById(a.getUserId());
                    if (userDetail != null) {
                        LocalDate localDate = LocalDate.now();
                        LocalDate preDate = localDate.minusMonths(1);
                        UserVacationCal calPre = userVacationCalService.getUserVacationCalByUserCode(a.getUserCode(), LocalDate.of(preDate.getYear(), preDate.getMonthValue(), 1));
                        UserVacationCal cal = userVacationCalService.getUserVacationCalByUserCode(a.getUserCode(), LocalDate.of(localDate.getYear(), localDate.getMonthValue(), 1));
                        UserVacation userVacation = userVacationService.findUserVacation(a.getUserId(), localDate.getYear());
                        UserVacation userVacationPre = userVacationService.findUserVacation(a.getUserId(), preDate.getYear());
                        List<Application> applies = applicationService.findMonthlyEffectiveApplications(a.getUserId());
                        List<Application> preApplies = applicationService.findMonthlyEffectiveApplicationsWithDate(a.getUserId(), preDate);
                        if (applies != null && applies.size() != 0) {
                            userVacationCalService.reCalForVacation(applies, userDetail, userVacation, a, cal);
                        }
                        if (preApplies != null && preApplies.size() != 0) {
                            userVacationCalService.reCalForVacation(preApplies, userDetail, userVacationPre, a, calPre);
                        }
                    }
                }catch (Exception e){
                  log.error("fail to cal user vacation",e);
                }
            });
        }
    }

    //@Scheduled(cron = "0 */1 * * * ? ")
    @Scheduled(cron = "0 0 1 1 1 ?") //一月一号一点执行
    public void rewriteRestAnnualLeaveJob() {
        //新年书写上年剩余年假
        log.info("1月1日进行上年剩余可休年假统计");
        List<User> activeUsers = userService.findAll();
        if (activeUsers != null && activeUsers.size() != 0) {
            activeUsers.forEach(a -> {
                UserVacation previosUserVacation = userVacationService.findUserVacation(a.getUserId(), LocalDate.now().getYear() - 1);
                UserVacation currentUserVacation = userVacationService.findUserVacation(a.getUserId(), LocalDate.now().getYear());
                if(Objects.isNull(currentUserVacation)){
                    currentUserVacation = userVacationService.saveDefaultVacation(a.getUserId());
                }
                if(Objects.nonNull(previosUserVacation)){
                    currentUserVacation.setRestAnnualLeave( previosUserVacation.getAnnualLeave());
                }else{
                    currentUserVacation.setRestAnnualLeave(0.0);
                }
            });
        }
    }


   // @Scheduled(cron = "0 0/2 * * * ?")
    @Scheduled(cron = "0 0 1 1 3 ?")
    public void removeRestAunnalLeaveJob() {
        //rest annual rest 清零
        log.info("2月末进行上年可休年假清零");
        List<User> activeUsers = userService.findAll();
        if (activeUsers != null && activeUsers.size() != 0) {
            activeUsers.forEach(a -> {
                UserVacation userVacation = userVacationService.findUserVacation(a.getUserId(), LocalDate.now().getYear());
                if (Objects.nonNull(userVacation)) {
                    userVacation.setRestAnnualLeave(0.0);
                    userVacationService.saveUserVacation(userVacation);
                }
            });
        }
    }

}
