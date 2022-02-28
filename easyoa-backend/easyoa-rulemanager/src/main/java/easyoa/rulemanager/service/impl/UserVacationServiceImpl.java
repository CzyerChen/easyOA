package easyoa.rulemanager.service.impl;

import easyoa.rulemanager.domain.LeaveRules;
import easyoa.rulemanager.domain.User;
import easyoa.rulemanager.domain.UserDetail;
import easyoa.rulemanager.domain.UserVacation;
import easyoa.rulemanager.service.ApplicationService;
import easyoa.rulemanager.service.LeaveRulesService;
import easyoa.rulemanager.service.UserVacationService;
import easyoa.common.constant.LeaveConstant;
import easyoa.common.utils.DateUtil;
import easyoa.rulemanager.repository.UserVacationRepository;
import easyoa.rulemanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by claire on 2019-07-17 - 09:58
 **/
@Service(value = "userVacationService")
public class UserVacationServiceImpl implements UserVacationService {
    @Autowired
    private UserVacationRepository userVacationRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private LeaveRulesService leaveRulesService;

    @Override
    public UserVacation findUserVacation(Long userId, Integer year) {
      /*  UserVacation userVacation = new UserVacation();
        userVacation.setUserId(userId);

        userVacation.setAlBackup();
        userVacation.setClBackup();
        userVacation.setFlBackup();
        userVacation.setMlBackup();
        userVacation.setMnlBackup();
        userVacation.setSlBackup();
        userVacation.setSlnBackup();
        userVacation.setPnlBackup();

        userVacation.setAnnualLeave();
        userVacation.setCasualLeave();
        userVacation.setFuneralLeave();
        userVacation.setMarriageLeave();
        userVacation.setMaternityLeave();
        userVacation.setPaternityLeave();
        userVacation.setSickLeave();*/

     /*   UserVacation currentVacation = userVacationRepository.findByUserIdAndYear(userId, LocalDate.now().getYear());
        if(Objects.nonNull(currentVacation)){

        }
        UserVacation previousVacation = userVacationRepository.findByUserIdAndYear(userId, LocalDate.now().getYear() - 1);
        if(Objects.nonNull(previousVacation)){

        }*/
        return userVacationRepository.findByUserIdAndYear(userId, year);
    }

    @Override
    public UserVacation saveUserVacation(UserVacation userVacation) {
        return userVacationRepository.save(userVacation);
    }

    @Override
    public UserVacation saveDefaultVacation(Long userId) {
        UserVacation userVacation = new UserVacation();
        userVacation.setUserId(userId);
        userVacation.setWeeklyApply(0);
        userVacation.setMonthlyApply(0);

        userVacation.setTotal(0.0);
        userVacation.setTotalAll(0.0);
        userVacation.setAnnualLeave(0.0);
        userVacation.setPaternityLeave(0.0);
        userVacation.setMaternityLeave(0.0);
        userVacation.setMarriageLeave(0.0);
        userVacation.setSickLeave(0.0);
        userVacation.setPaternityLeave(0.0);
        userVacation.setCasualLeave(0.0);
        userVacation.setSickLeaveNormal(0.0);
        userVacation.setFuneralLeave(0.0);

        userVacation.setAlBackup(0.0);
        userVacation.setPnlBackup(0.0);
        userVacation.setMnlBackup(0.0);
        userVacation.setMlBackup(0.0);
        userVacation.setSlBackup(0.0);
        userVacation.setPnlBackup(0.0);
        userVacation.setClBackup(0.0);
        userVacation.setSlnBackup(0.0);
        userVacation.setFlBackup(0.0);

        userVacation.setYear(LocalDate.now().getYear());



        UserDetail userDetail = userService.getUserDetailById(userId);
        double workYear = userDetail.getWorkYear();
        //int age = userDetail.getAge();

        User user = userService.getUser(userId);
        Character sex = user.getSex();

        double count = 0;
        List<LeaveRules> allRules = leaveRulesService.findAllRules(user.getCompanyId());

        /**
         * 基础假
         */
        //年假
        allRules.stream()
                .filter(r -> LeaveConstant.ANNUAL_LEAVE.equals(r.getRuleName())
                        && workYear > r.getWorkYearsFrom() && workYear <= r.getWorkYearsTo())
                .findFirst()
                .ifPresent(r1 -> {
                    Calendar instance = Calendar.getInstance();
                    instance.setTime(userDetail.getHireDate());

                    if ((instance.get(Calendar.YEAR)) == LocalDate.now().getYear()) {
                        Duration duration = Duration.between(DateUtil.dateConvertToLocalDateTime(userDetail.getHireDate()),LocalDateTime.of(LocalDate.now().getYear(), 12, 31, 23, 59, 59));
                        long round = Math.round(duration.toDays() * 1.0 / 365 * r1.getMaxPermitDay());
                        userVacation.setAnnualLeave(Double.valueOf(String.valueOf(round)));
                    } else {
                        userVacation.setAnnualLeave(r1.getMaxPermitDay().doubleValue());
                    }
                });
        //事假
        allRules.stream().filter(r -> LeaveConstant.CASUAL_LEAVE.equals(r.getRuleName()))
                .max(Comparator.comparingInt(LeaveRules::getMaxPermitDay))
                .ifPresent(r1 -> userVacation.setCasualLeave(r1.getMaxPermitDay().doubleValue()));

        if(!userDetail.getId().startsWith("OA9")) {
            //带薪病假
            allRules.stream().filter(r -> LeaveConstant.SICK_LEAVE.equals(r.getRuleName())).max(Comparator.comparingInt(LeaveRules::getMaxPermitDay))
                    .ifPresent(r1 -> {
                        Calendar instance = Calendar.getInstance();
                        instance.setTime(userDetail.getHireDate());

                        if (instance.get(Calendar.YEAR) == LocalDate.now().getYear()) {
                            Duration duration = Duration.between(DateUtil.dateConvertToLocalDateTime(userDetail.getHireDate()),LocalDateTime.of(LocalDate.now().getYear(), 12, 31, 23, 59, 59));
                            long round = Math.round(duration.toDays() * 1.0 / 365 * r1.getMaxPermitDay());
                            userVacation.setSickLeave(Double.valueOf(String.valueOf(round)));
                        } else {
                            userVacation.setSickLeave(r1.getMaxPermitDay().doubleValue());
                        }
                    });
        }
        //普通病假
        allRules.stream().filter(r -> LeaveConstant.SICK_LEAVE_NORMAL.equals(r.getRuleName())).max(Comparator.comparingInt(LeaveRules::getMaxPermitDay))
                .ifPresent(r1 -> { userVacation.setSickLeaveNormal(r1.getMaxPermitDay().doubleValue()); });
        count += userVacation.getAnnualLeave() + userVacation.getCasualLeave() + userVacation.getSickLeave()+userVacation.getSickLeaveNormal();

        /**
         * 特殊假
         */
        if(!userDetail.getId().startsWith("OA9")) {
            //丧假
            allRules.stream().filter(r -> LeaveConstant.FUNERAL_LEAVE.equals(r.getRuleName()))
                    .findFirst()
                    .ifPresent(r1 -> userVacation.setFuneralLeave(r1.getMaxPermitDay().doubleValue()));

            userVacation.setFuneralLeave(3.0);//丧假

            //婚假
            allRules.stream().filter(r -> LeaveConstant.MARRIAGE_LEAVE.equals(r.getRuleName()))
                    .findFirst()
                    .ifPresent(r1 -> {
                        if (!userDetail.getMarriage()) {//(age >= 22 && sex == 'M') || (age >= 20 && sex == 'F')
                            userVacation.setMarriageLeave(r1.getMaxPermitDay().doubleValue());
                        }
                    });
            //产假
            allRules.stream().filter(r -> LeaveConstant.MATERNITY_LEAVE.equals(r.getRuleName()))
                    .max(Comparator.comparingInt(LeaveRules::getMaxPermitDay))
                    .ifPresent(r1 -> {
                        if (sex == 'F') {
                            userVacation.setMaternityLeave(r1.getMaxPermitDay().doubleValue());
                        }
                    });
            //陪产假
            allRules.stream().filter(r -> LeaveConstant.PATERNITY_LEAVE.equals(r.getRuleName()))
                    .findFirst()
                    .ifPresent(r1 -> {
                        if (sex == 'M') {
                            userVacation.setPaternityLeave(r1.getMaxPermitDay().doubleValue());
                        }
                    });
        }
        count += userVacation.getFuneralLeave()
                + userVacation.getMarriageLeave()
                + userVacation.getMaternityLeave()
                + userVacation.getPaternityLeave();


        Integer count1 = applicationService.findMonthlyEffectiveApplicationsCount(userId);
        Integer count2 = applicationService.findWeeklyEffectiveApplicationsCount(userId);
        userVacation.setWeeklyApply(count1 == null ? 0 : count1);
        userVacation.setMonthlyApply(count2 == null ? 0 : count2);

        userVacation.setTotal(count);
        userVacation.setTotalAll(count);

        userVacation.setAnnualShould(userVacation.getAnnualLeave());
        UserVacation previousVacation = userVacationRepository.findByUserIdAndYear(userId, LocalDate.now().getYear() - 1);
        if(Objects.nonNull(previousVacation)){
            userVacation.setRestAnnualLeave(previousVacation.getAnnualLeave());
        }else{
            userVacation.setRestAnnualLeave(0.0);
        }
        return saveUserVacation(userVacation);
    }

    @Override
    public void updateUserVacationInfo(LeaveRules oldRule, LeaveRules newRule) {
        UserVacation data = new UserVacation();
        double count = newRule.getMaxPermitDay().doubleValue() - oldRule.getMaxPermitDay();
        switch (newRule.getRuleName()) {
            case LeaveConstant.ANNUAL_LEAVE:
                data.setAnnualLeave(count);
                break;
            case LeaveConstant.CASUAL_LEAVE:
                data.setCasualLeave(count);
                break;
            case LeaveConstant.SICK_LEAVE:
                data.setSickLeave(count);
                break;
            case LeaveConstant.FUNERAL_LEAVE:
                data.setFuneralLeave(count);
                break;
            case LeaveConstant.MARRIAGE_LEAVE:
                data.setMarriageLeave(count);
                break;
            case LeaveConstant.MATERNITY_LEAVE:
                data.setMaternityLeave(count);
                break;
            case LeaveConstant.PATERNITY_LEAVE:
                data.setPaternityLeave(count);
            case LeaveConstant.SICK_LEAVE_NORMAL:
                data.setSickLeaveNormal(count);
                break;
            default:
        }

        List<UserVacation> vacations = findAllUserVacation();
        if (vacations != null && vacations.size() != 0) {
            vacations.forEach(v -> {
                if (data.getAnnualLeave() != 0) {
                    v.setAnnualLeave(v.getAnnualLeave() + data.getAnnualLeave());
                }
                if (data.getCasualLeave() != 0) {
                    v.setCasualLeave(v.getCasualLeave() + data.getCasualLeave());
                }
                if (data.getMaternityLeave() != 0) {
                    v.setMaternityLeave(v.getMaternityLeave() + data.getMaternityLeave());
                }
                if (data.getSickLeave() != 0) {
                    v.setSickLeave(v.getSickLeave() + data.getSickLeave());
                }
                if (data.getPaternityLeave() != 0) {
                    v.setPaternityLeave(v.getPaternityLeave() + data.getPaternityLeave());
                }
                if (data.getMarriageLeave() != 0) {
                    v.setMarriageLeave(v.getMarriageLeave() + data.getMarriageLeave());
                }
                if (data.getFuneralLeave() != 0) {
                    v.setFuneralLeave(v.getFuneralLeave() + data.getFuneralLeave());
                }
                if(data.getSickLeaveNormal() !=0){
                    v.setSickLeaveNormal(v.getSickLeaveNormal()+data.getSickLeaveNormal());
                }
            });
            userVacationRepository.saveAll(vacations);
        }
    }

    @Override
    public List<UserVacation> findAllUserVacation() {
        return userVacationRepository.findAll();
    }
}
