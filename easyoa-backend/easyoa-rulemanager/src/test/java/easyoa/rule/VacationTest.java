package easyoa.rule;

import easyoa.rulemanager.domain.*;
import easyoa.rulemanager.service.*;
import easyoa.common.constant.LeaveConstant;
import easyoa.common.utils.DateUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-07-22 - 10:34
 **/
public class VacationTest extends AbstractApplicationTest {

    @Autowired
    private UserVacationService userVacationService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserVacationCalService userVacationCalService;
    @Autowired
    private LeaveRulesService leaveRulesService;

    @Test
    public void testVacationCal() {
        List<User> activeUsers = userService.findAll();
        List<Long> ids = activeUsers.stream().map(User::getUserId).distinct().collect(Collectors.toList());
        ids.forEach(id -> {
            UserVacation userVacation = userVacationService.findUserVacation(id,LocalDate.now().getYear());
            if (userVacation == null) {
                userVacation = userVacationService.saveDefaultVacation(id);
            }

            Integer count1 = applicationService.findWeeklyEffectiveApplicationsCount(id);
            Integer count2 = applicationService.findMonthlyEffectiveApplicationsCount(id);
            Double count3 = userVacation.getAnnualLeave()
                    + userVacation.getCasualLeave()
                    + userVacation.getFuneralLeave()
                    + userVacation.getMarriageLeave()
                    + userVacation.getSickLeave()
                    + userVacation.getMaternityLeave()
                    + userVacation.getPaternityLeave()
                    + userVacation.getSickLeaveNormal();
            userVacation.setWeeklyApply(count1);
            userVacation.setMonthlyApply(count2);
            userVacation.setTotal(count3);

            userVacationService.saveUserVacation(userVacation);
        });
    }

    @Test
    public void testTime(){
        Duration duration = Duration.between(DateUtil.dateConvertToLocalDateTime(new Date()), LocalDateTime.of(LocalDate.now().getYear(), 12, 31,23,59,59));
        long l = duration.toDays();
        Long round = Math.round(l*1.0 / 365*5);
    }

    @Test
    public void testUserVacationInsert(){
        for(int i=36;i<64;i++){
            userVacationService.saveDefaultVacation((long) i);
        }
    }

    @Test
    public void testUserVacationCal(){
        List<User> activeUsers = userService.findAll();
        activeUsers.forEach(a ->{
            UserVacation userVacation = userVacationService.findUserVacation(a.getUserId(),LocalDate.now().getYear());
            userVacationCalService.saveDefaultVacationCal(userVacation);
        });
    }


    @Test
    public void testUserVacationCal1(){
        List<User> activeUsers = userService.findAll();
        if (activeUsers != null && activeUsers.size() != 0) {
            activeUsers.forEach(a -> {
                UserDetail userDetail = userService.getUserDetailById(a.getUserId());
                if (userDetail != null) {
                    UserVacationCal cal = userVacationCalService.getUserVacationCalByUserCode(a.getUserCode(),LocalDate.of(2019,8,1));
                    List<LeaveRules> allRules = leaveRulesService.findAllRules(null);
                    UserVacation userVacation = userVacationService.findUserVacation(a.getUserId(),LocalDate.now().getYear());
                    List<Application> applies = applicationService.findMonthlyEffectiveApplications(a.getUserId());
                    if (applies != null && applies.size() != 0) {
                        //年假
                        Double anual = applies.stream().filter(p -> LeaveConstant.ANNUAL_LEAVE.equals(p.getLeaveType())).mapToDouble(Application::getDays).sum();
                        //丧假
                        Double funeral = applies.stream().filter(p -> LeaveConstant.FUNERAL_LEAVE.equals(p.getLeaveType())).mapToDouble(Application::getDays).sum();
                        //带薪病假
                        Double sick = applies.stream().filter(p -> LeaveConstant.SICK_LEAVE.equals(p.getLeaveType())).mapToDouble(Application::getDays).sum();
                        //普通病假
                        Double sickNormal = applies.stream().filter(p -> LeaveConstant.SICK_LEAVE_NORMAL.equals(p.getLeaveType())).mapToDouble(Application::getDays).sum();
                        //事假
                        Double casual = applies.stream().filter(p -> LeaveConstant.CASUAL_LEAVE.equals(p.getLeaveType())).mapToDouble(Application::getDays).sum();
                        //婚假
                        Double marriage = applies.stream().filter(p -> LeaveConstant.MARRIAGE_LEAVE.equals(p.getLeaveType())).mapToDouble(Application::getDays).sum();
                        //产假
                        Double maternity = applies.stream().filter(p -> LeaveConstant.MATERNITY_LEAVE.equals(p.getLeaveType())).mapToDouble(Application::getDays).sum();
                        //陪产假
                        Double paternity = applies.stream().filter(p -> LeaveConstant.PATERNITY_LEAVE.equals(p.getLeaveType())).mapToDouble(Application::getDays).sum();
                        //产检假
                        Double maternity4 = applies.stream().filter(p -> LeaveConstant.MATERNITY_LEAVE_4.equals(p.getLeaveType())).mapToDouble(Application::getDays).sum();

                        cal.setAnnual(anual);
                        cal.setSick(sick);
                        cal.setSickNormal(sickNormal);
                        cal.setCasual(casual);
                        cal.setMarriage(marriage);
                        cal.setFuneral(funeral);
                        if('F' == a.getSex()){
                            cal.setMaterPaternity(maternity);
                        }else if('M' == a.getSex()){
                            cal.setMaterPaternity(paternity);
                        }
                        cal.setMaternity4(maternity4);

                        if(allRules != null && allRules.size() !=0) {
                            Integer annualShould = allRules.stream()
                                    .filter(r -> LeaveConstant.ANNUAL_LEAVE.equals(r.getRuleName())
                                            && userDetail.getWorkYear() > r.getWorkYearsFrom() && userDetail.getWorkYear() <= r.getWorkYearsTo())
                                    .findFirst().map(LeaveRules::getMaxPermitDay).orElse(0);

                            Double annualCal = allRules.stream()
                                    .filter(r -> LeaveConstant.ANNUAL_LEAVE.equals(r.getRuleName())
                                            && userDetail.getWorkYear() > r.getWorkYearsFrom() && userDetail.getWorkYear() <= r.getWorkYearsTo())
                                    .findFirst()
                                    .map(r1 -> {
                                        if ((userDetail.getHireDate().getYear() + 1900) == LocalDate.now().getYear()) {
                                            Duration duration = Duration.between(DateUtil.dateConvertToLocalDateTime(userDetail.getHireDate()),LocalDateTime.of(LocalDate.now().getYear(), 12, 31, 23, 59, 59));//, ;
                                            long round = Math.round(duration.toDays() * 1.0 / 365 * r1.getMaxPermitDay());
                                            return Double.valueOf(String.valueOf(round));
                                        } else {
                                            return r1.getMaxPermitDay().doubleValue();
                                        }
                                    }).orElse(0.0);
                            cal.setAnnualShould(annualShould.doubleValue());
                            cal.setAnnualCal(annualCal);
                        }

                        if(userVacation != null) {
                            cal.setAnnualRest(userVacation.getAnnualLeave());
                        }
                        cal.setCalculateTime(LocalDateTime.now());
                        userVacationCalService.updateVacationCal(cal);
                    }
                }
            });
        }
    }
}

