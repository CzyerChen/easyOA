package easyoa.rulemanager.model.message;

import easyoa.rulemanager.constant.ContextConstant;
import easyoa.rulemanager.constant.RuleConstant;
import easyoa.rulemanager.domain.Application;
import easyoa.rulemanager.domain.User;
import easyoa.rulemanager.domain.UserDetail;
import easyoa.rulemanager.domain.UserVacation;
import easyoa.rulemanager.model.SpringContextHolder;
import easyoa.rulemanager.service.CacheService;
import easyoa.rulemanager.service.UserService;
import easyoa.rulemanager.service.UserVacationService;
import easyoa.common.domain.vo.LeaveRuleVO;
import easyoa.common.model.message.ApplyMessage;
import easyoa.common.model.message.FeedBack;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created by claire on 2019-07-10 - 11:25
 **/
@Slf4j
public class ApplyMessageHandler extends MessageHandler<ApplyMessage, FeedBack, Application> {

    @Override
    public FeedBack handle(ApplyMessage message, Long key) {
        log.info("ApplyMessageHandler proceesor ");
       /* UserService userService = SpringContextHolder.getBean(ContextConstant.USER);
        UserDetail detail = userService.getUserDetailById(key);

        CacheService cacheService = SpringContextHolder.getBean(ContextConstant.CACHE);
        List<LeaveRuleVO> rules = cacheService.getRulesByKey(message.getName());

        UserVacationService userVacationService = SpringContextHolder.getBean(ContextConstant.VACATION);
        UserVacation userVacation = userVacationService.findUserVacation(key, LocalDate.now().getYear());
        if (userVacation == null) {
            userVacation = userVacationService.saveDefaultVacation(key);
        }*/

        FeedBack feedBack = new FeedBack(message.getName(), message.getType(), message.getDays());

        return feedBack;
    }

    @Override
    public FeedBack handle(Application application) throws Exception {
        log.info("ApplyMessageHandler proceesor for application");
        FeedBack feedBack = new FeedBack(application.getLeaveType(), application.getApplicateType(), application.getDays());

        try {
            UserService userService = SpringContextHolder.getBean(ContextConstant.USER);
            UserDetail detail = userService.getUserDetailById(application.getUserId());

            CacheService cacheService = SpringContextHolder.getBean(ContextConstant.CACHE);
            User user = userService.getUser(application.getUserId());
            List<LeaveRuleVO> rules = cacheService.getRulesByKey(user.getCompanyId() + application.getLeaveType());

            UserVacationService userVacationService = SpringContextHolder.getBean(ContextConstant.VACATION);
            UserVacation userVacation = userVacationService.findUserVacation(application.getUserId(), LocalDate.now().getYear());
            if (userVacation == null) {
                userVacation = userVacationService.saveDefaultVacation(application.getUserId());
            }
            switch (application.getLeaveType()) {

                case RuleConstant.CASUAL_LEAVE:
                    Optional<LeaveRuleVO> first = rules.stream().filter(l -> l.getCompanyId().equals(user.getCompanyId()) &&
                            l.getLeaveDaysFrom() <= application.getDays() && l.getLeaveDaysTo() > application.getDays()).findFirst();
                    if (first.isPresent()) {
                        LeaveRuleVO r = first.get();
                        if (userVacation.getClBackup() > userVacation.getCasualLeave()) {
                            feedBack.setMeet(false);
                            feedBack.setInvalidMessage("事假时长累计申请已超出规定，拒绝申请，请检查现有申请是否都已结束或与人事部门联系");
                        } else {//判断提前申请时间的要求是否符合，但是还是会允许申请
                            feedBack.setMeet(true);
                            LocalDateTime minusDays = application.getStartTime().minusDays(r.getForwardDays());
                            if (minusDays.isBefore(LocalDateTime.now())) {//当日期与LocalDate.now相等时候返回false ,与预期相符合
                                feedBack.setTimeMeet(false);
                            } else {
                                feedBack.setTimeMeet(true);
                            }
                        }
                        feedBack.setRequestMessage(r.getNotice());
                    }
                    break;

                case RuleConstant.SICK_LEAVE:
                    LeaveRuleVO r = rules.get(0);
                    boolean meetSick = Boolean.FALSE;
                    String errorMsgSick = "";
                    boolean timeMeetSick = Boolean.TRUE;
                    if (detail.getWorkYear() != 0) {
                        if (userVacation.getSlBackup() > userVacation.getSickLeave()) {
                            timeMeetSick = Boolean.FALSE;
                            errorMsgSick = "带薪病假时长累计申请已超出规定，拒绝申请，请检查现有申请是否都已结束或与人事部门联系";
                        } else {//判断提前申请时间的要求是否符合，但是还是会允许申请
                            meetSick = Boolean.TRUE;
                        }
                    } else {
                        timeMeetSick = Boolean.FALSE;
                        errorMsgSick = "实习生暂无带薪病假可申请，可重新选择事假进行申请";
                    }
                    feedBack.setMeet(meetSick);
                    feedBack.setTimeMeet(timeMeetSick);
                    feedBack.setInvalidMessage(errorMsgSick);
                    feedBack.setRequestMessage(r.getNotice());
                    break;

                case RuleConstant.SICK_LEAVE_NORMAL:
                    LeaveRuleVO r0 = rules.get(0);
                    if (userVacation.getSlnBackup() > userVacation.getSickLeaveNormal()) {
                        feedBack.setMeet(false);
                        feedBack.setInvalidMessage("普通病假时长累计申请已超出规定，拒绝申请，请检查现有申请是否都已结束或与人事部门联系");
                        feedBack.setTimeMeet(false);
                    } else {
                        feedBack.setMeet(true);
                        feedBack.setTimeMeet(true);
                    }
                    feedBack.setRequestMessage(r0.getNotice());

                    break;

                case RuleConstant.ANNUAL_LEAVE:
                    Optional<LeaveRuleVO> first1 = rules.stream().filter(l -> detail.getWorkYear() > l.getWorkYearsFrom() && detail.getWorkYear() < l.getWorkYearsTo()).findFirst();
                    boolean meetAnnual = Boolean.FALSE;
                    String errorMsgAnnual = "";
                    boolean timeMeetAnnual = Boolean.TRUE;

                    if (first1.isPresent()) {
                        LeaveRuleVO r1 = first1.get();
                        if (application.getStartTime().isBefore(LocalDateTime.of(application.getStartTime().getYear(), 7, 1, 0, 0))) {
                            //上半年包含年假和年假一般折算
                            //含有年假剩余，3月1日之前
                            if (userVacation.getAlBackup() >
                                    (userVacation.getAnnualShould() / 2 - (userVacation.getAnnualShould() - userVacation.getAnnualLeave()))
                                            + userVacation.getRestAnnualLeave()) {
                                //拒绝申请
                                timeMeetAnnual = Boolean.FALSE;
                                errorMsgAnnual = "年假时长累计申请已超出规定，拒绝申请，【注意：年假可休时长以半年为单位，申请可休天数会折半计算】";
                            } else {
                                //同意申请
                                meetAnnual = Boolean.TRUE;
                            }
                        } else {
                            //下半年就没有限制，按照原有方式剩余假期来请假
                            if (userVacation.getAlBackup() > userVacation.getAnnualLeave()) {
                                timeMeetAnnual = Boolean.FALSE;
                                errorMsgAnnual = "年假时长累计申请已超出规定，拒绝申请，请检查现有申请是否都已结束或与人事部门联系";
                            } else {//判断提前申请时间的要求是否符合，但是还是会允许申请
                                meetAnnual = Boolean.TRUE;
                            }
                        }

                        feedBack.setRequestMessage(r1.getNotice());
                    } else {
                        errorMsgAnnual = "实习生暂无年假可申请，可重新选择事假进行申请";
                    }
                    feedBack.setMeet(meetAnnual);
                    feedBack.setTimeMeet(timeMeetAnnual);
                    feedBack.setInvalidMessage(errorMsgAnnual);
                    break;


                case RuleConstant.MARRIAGE_LEAVE:
                    LeaveRuleVO r2 = rules.get(0);
                    if (!detail.getMarriage()) {
                        if (userVacation.getMlBackup() <= userVacation.getMarriageLeave()) {
                            feedBack.setMeet(true);
                            if (application.getStartTime().minusDays(r2.getForwardDays()).isBefore(LocalDateTime.now())) {
                                feedBack.setTimeMeet(true);
                            } else {
                                feedBack.setTimeMeet(false);
                            }
                            feedBack.setTimeMeet(true);
                        } else {
                            feedBack.setMeet(false);
                            feedBack.setTimeMeet(false);
                            feedBack.setInvalidMessage("婚假时长3天，当前时长已超出规定，拒绝申请，请分开填写请假单");
                        }
                    } else {
                        feedBack.setMeet(false);
                        feedBack.setTimeMeet(false);
                        feedBack.setInvalidMessage("婚假拒绝申请");
                    }
                    feedBack.setRequestMessage(r2.getNotice());
                    break;

                case RuleConstant.MATERNITY_LEAVE:
                    LeaveRuleVO r3 = rules.stream().max(Comparator.comparingInt(LeaveRuleVO::getMaxPermitDay)).get();
                    Character userSex1 = userService.getUserSex(application.getUserId());
                    if (userSex1 == 'F' && userVacation.getMnlBackup() <= userVacation.getMaternityLeave()) {
                        feedBack.setMeet(true);
                        feedBack.setTimeMeet(true);
                    } else {
                        feedBack.setMeet(false);
                        feedBack.setTimeMeet(false);
                        feedBack.setInvalidMessage("产假条件不符合/产休可申请时间不足，拒绝申请");
                    }
                    feedBack.setRequestMessage(r3.getNotice());
                    break;

                case RuleConstant.PATERNITY_LEAVE:
                    LeaveRuleVO r4 = rules.stream().max(Comparator.comparingInt(LeaveRuleVO::getMaxPermitDay)).get();
                    Character userSex2 = userService.getUserSex(application.getUserId());
                    if (userSex2 == 'M' && userVacation.getPnlBackup() <= userVacation.getPaternityLeave()) {
                        feedBack.setMeet(true);
                        feedBack.setTimeMeet(true);
                    } else {
                        feedBack.setMeet(false);
                        feedBack.setTimeMeet(false);
                        feedBack.setInvalidMessage("陪产假条件不符合/陪产假时间不足，拒绝申请");
                    }
                    feedBack.setRequestMessage(r4.getNotice());
                    break;


                case RuleConstant.FUNERAL_LEAVE:
                    LeaveRuleVO r5 = rules.get(0);
                    if (userVacation.getFlBackup() <= userVacation.getFuneralLeave()) {
                        feedBack.setMeet(true);
                        feedBack.setTimeMeet(true);
                    } else {
                        feedBack.setMeet(false);
                        feedBack.setTimeMeet(false);
                        feedBack.setInvalidMessage("丧假时长3天，当前时长不符合，拒绝申请，请分开填写请假单");
                    }
                    feedBack.setRequestMessage(r5.getNotice());
                    break;
                default:
            }
        }catch (Exception e){
            feedBack.error();
        }
        return feedBack;
    }
}
