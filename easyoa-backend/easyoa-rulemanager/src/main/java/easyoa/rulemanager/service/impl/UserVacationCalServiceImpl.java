package easyoa.rulemanager.service.impl;

import easyoa.common.constant.LeaveConstant;
import easyoa.common.utils.DateUtil;
import easyoa.rulemanager.domain.*;
import easyoa.rulemanager.repository.UserVacationCalRepository;
import easyoa.rulemanager.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by claire on 2019-08-06 - 16:39
 **/
@Slf4j
@Service("userVacationCalService")
public class UserVacationCalServiceImpl implements UserVacationCalService {
    @Autowired
    private UserVacationCalRepository userVacationCalRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private LeaveRulesService leaveRulesService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private UserVacationService userVacationService;

    @Override
    public UserVacationCal getUserVacationCalByUserCode(String userCode, LocalDate month) {
        return userVacationCalRepository.findByUserCodeAndCalculateMonth(userCode, month);
    }

    @Override
    public void saveDefaultVacationCal(UserVacation userVacation) {
        UserVacationCal cal = new UserVacationCal();
        UserDetail userDetail = userService.getUserDetailById(userVacation.getUserId());
        User user = userService.getUser(userVacation.getUserId());
        List<LeaveRules> allRules = leaveRulesService.findAllRules(user.getCompanyId());
        if (userDetail != null) {
            cal.setUserId(userDetail.getUserId());
            cal.setUserCode(userDetail.getId());
            cal.setUserName(userDetail.getChineseName());
            cal.setHireDate(DateUtil.dateConvertToLocalDate(userDetail.getHireDate()));
            cal.setAnnual(0.0);
            cal.setCasual(0.0);
            cal.setSick(0.0);
            cal.setSickNormal(0.0);
            cal.setMarriage(0.0);
            cal.setMaterPaternity(0.0);
            cal.setMaternity4(0.0);
            cal.setFuneral(0.0);
            cal.setAnnualRest(userVacation.getAnnualLeave());
            cal.setCalculateMonth(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1));
            cal.setCalculateTime(LocalDateTime.now());

            if (allRules != null && allRules.size() != 0) {
                Integer annualShould = allRules.stream()
                        .filter(r -> LeaveConstant.ANNUAL_LEAVE.equals(r.getRuleName())
                                && userDetail.getWorkYear() > r.getWorkYearsFrom() && userDetail.getWorkYear() <= r.getWorkYearsTo())
                        .findFirst().map(LeaveRules::getMaxPermitDay).orElse(0);
                cal.setAnnualShould(annualShould.doubleValue());

                allRules.stream()
                        .filter(r -> LeaveConstant.ANNUAL_LEAVE.equals(r.getRuleName())
                                && userDetail.getWorkYear() > r.getWorkYearsFrom() && userDetail.getWorkYear() <= r.getWorkYearsTo())
                        .findFirst()
                        .ifPresent(r1 -> {
                            if ((userDetail.getHireDate().getYear()+1900) == LocalDate.now().getYear()) {
                                Duration duration = Duration.between(DateUtil.dateConvertToLocalDateTime(userDetail.getHireDate()),
                                        LocalDateTime.of(LocalDate.now().getYear(), 12, 31, 23, 59, 59));//);
                                long round = Math.round(duration.toDays() * 1.0 / 365 * r1.getMaxPermitDay());
                                cal.setAnnualCal(Double.valueOf(String.valueOf(round)));
                            } else {
                                cal.setAnnualCal(r1.getMaxPermitDay().doubleValue());
                            }
                        });
            }
            userVacationCalRepository.save(cal);
        }
    }

    @Override
    public void updateVacationCal(UserVacationCal userVacationCal) {
        userVacationCalRepository.save(userVacationCal);
    }

    @Override
    public void calForUser(UserDetail userDetail, LocalDate month) {
        UserVacationCal cal = userVacationCalRepository.findByUserCodeAndCalculateMonth(userDetail.getId(), month);
        if(cal != null){
            User user = userService.getUser(userDetail.getUserId());
            List<LeaveRules> allRules = leaveRulesService.findAllRules(user.getCompanyId());
            UserVacation userVacation = userVacationService.findUserVacation(userDetail.getUserId(),LocalDate.now().getYear());
            List<Application> applies = applicationService.findMonthlyEffectiveApplications(userDetail.getUserId());
            if (applies != null && applies.size() != 0) {
                //年假
                Double anual = applies.stream().filter(p -> LeaveConstant.ANNUAL_LEAVE.equals(p.getLeaveType())).mapToDouble(Application::getDays).sum();
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
                if (userDetail.getSex()) {
                    cal.setMaterPaternity(maternity);
                } else {
                    cal.setMaterPaternity(paternity);
                }
                cal.setMaternity4(maternity4);

                if (allRules != null && allRules.size() != 0 && userDetail != null) {
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
                                    Duration duration = Duration.between(DateUtil.dateConvertToLocalDateTime(userDetail.getHireDate()),LocalDateTime.of(LocalDate.now().getYear(), 12, 31, 23, 59, 59));
                                            //);
                                    long round = Math.round(duration.toDays() * 1.0 / 365 * r1.getMaxPermitDay());
                                    return Double.valueOf(String.valueOf(round));
                                } else {
                                    return r1.getMaxPermitDay().doubleValue();
                                }
                            }).orElse(0.0);
                    cal.setAnnualShould(annualShould.doubleValue());
                    cal.setAnnualCal(annualCal);
                }

                if (userVacation != null) {
                    cal.setAnnualRest(userVacation.getAnnualLeave());
                }
                cal.setCalculateTime(LocalDateTime.now());
                updateVacationCal(cal);
            }
        }

    }

    @Override
    public void reCalForVacation(List<Application> applies, UserDetail userDetail, UserVacation userVacation, User user,UserVacationCal cal) {
        //年假
        Double anual = applies.stream().filter(p -> LeaveConstant.ANNUAL_LEAVE.equals(p.getLeaveType())).mapToDouble(Application::getDays).sum();
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
        if('F' == user.getSex()){
            cal.setMaterPaternity(maternity);
        }else if('M' == user.getSex()){
            cal.setMaterPaternity(paternity);
        }
        cal.setMaternity4(maternity4);

        List<LeaveRules> allRules = leaveRulesService.findAllRules(user.getCompanyId());
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
                            Duration duration = Duration.between(DateUtil.dateConvertToLocalDateTime(userDetail.getHireDate()),
                                    LocalDateTime.of(LocalDate.now().getYear(), 12, 31, 23, 59, 59));
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
        if(cal != null){
            updateVacationCal(cal);
        }
    }
}
