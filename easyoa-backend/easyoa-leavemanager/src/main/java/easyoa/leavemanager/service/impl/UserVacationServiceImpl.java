package easyoa.leavemanager.service.impl;

import easyoa.leavemanager.domain.biz.Application;
import easyoa.leavemanager.domain.dto.ApplicationModifyDTO;
import easyoa.leavemanager.domain.user.UserVacation;
import easyoa.leavemanager.handler.InvokeStrategyContext;
import easyoa.leavemanager.service.UserVacationService;
import easyoa.common.constant.ApplicationStatusEnum;
import easyoa.common.constant.LeaveConstant;
import easyoa.common.constant.LeaveTypeEnum;
import easyoa.common.domain.vo.UserVacationVO;
import easyoa.leavemanager.repository.biz.ApplicationRepository;
import easyoa.leavemanager.repository.user.UserVacationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by claire on 2019-07-22 - 10:52
 **/
@Slf4j
@Service(value = "userVacationService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserVacationServiceImpl implements UserVacationService {
    @Autowired
    private UserVacationRepository userVacationRepository;
    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public UserVacation findByUserId(Long userId, int year) {
        return userVacationRepository.findByUserIdAndYear(userId, year);
    }

    @Override
    public void saveUserVacartion(UserVacation userVacation) {
        userVacationRepository.save(userVacation);
    }

    @Override
    public Double realizeVacationCache(Application application) {
        Double previousDays = 0.0;
        UserVacation userVacation = findByUserId(application.getUserId(), LocalDate.now().getYear());
        if (application.getDays() > userVacation.getRestAnnualLeave()) {
            //申请时长大于剩余年假，则需要修改剩余年假的total值
            userVacation.setTotal(userVacation.getTotal() - (application.getDays() - userVacation.getRestAnnualLeave()));
        }
        switch (application.getLeaveType()) {
            case LeaveConstant.ANNUAL_LEAVE:
                if (application.getDays() > userVacation.getRestAnnualLeave()) {
                    previousDays = userVacation.getRestAnnualLeave();
                    double rest = application.getDays() - userVacation.getRestAnnualLeave();
                    userVacation.setAlBackup(userVacation.getAlBackup() - application.getDays());
                    userVacation.setAnnualLeave(userVacation.getAnnualLeave() - rest);
                    userVacation.setRestAnnualLeave(0.0);
                    application.setPreviousDays(userVacation.getRestAnnualLeave());
                } else {
                    previousDays = application.getDays();
                    userVacation.setAlBackup(userVacation.getAlBackup() - application.getDays());
                    userVacation.setRestAnnualLeave(userVacation.getRestAnnualLeave() - application.getDays());
                    application.setPreviousDays(application.getDays());
                }
                break;
            case LeaveConstant.SICK_LEAVE:
                userVacation.setSlBackup(userVacation.getSlBackup() - application.getDays());
                userVacation.setSickLeave(userVacation.getSickLeave() - application.getDays());
                break;
            case LeaveConstant.CASUAL_LEAVE:
                userVacation.setClBackup(userVacation.getClBackup() - application.getDays());
                userVacation.setCasualLeave(userVacation.getCasualLeave() - application.getDays());
                break;
            case LeaveConstant.MARRIAGE_LEAVE:
                userVacation.setMlBackup(userVacation.getMlBackup() - application.getDays());
                userVacation.setMarriageLeave(userVacation.getMarriageLeave() - application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_1:
                userVacation.setMnlBackup(userVacation.getMnlBackup() - application.getDays());
                userVacation.setMaternityLeave(userVacation.getMaternityLeave() - application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_2:
                userVacation.setMnlBackup(userVacation.getMnlBackup() - application.getDays());
                userVacation.setMaternityLeave(userVacation.getMaternityLeave() - application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_3:
                userVacation.setMnlBackup(userVacation.getMnlBackup() - application.getDays());
                userVacation.setMaternityLeave(userVacation.getMaternityLeave() - application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_4:
                userVacation.setMnlBackup(userVacation.getMnlBackup() - application.getDays());
                userVacation.setMaternityLeave(userVacation.getMaternityLeave() - application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_5:
                userVacation.setMnlBackup(userVacation.getMnlBackup() - application.getDays());
                userVacation.setMaternityLeave(userVacation.getMaternityLeave() - application.getDays());
                break;
            case LeaveConstant.FUNERAL_LEAVE:
                userVacation.setFlBackup(userVacation.getFlBackup() - application.getDays());
                userVacation.setFuneralLeave(userVacation.getFuneralLeave() - application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE:
                userVacation.setMnlBackup(userVacation.getMnlBackup() - application.getDays());
                userVacation.setMaternityLeave(userVacation.getMaternityLeave() - application.getDays());
                break;
            case LeaveConstant.PATERNITY_LEAVE:
                userVacation.setPnlBackup(userVacation.getPnlBackup() - application.getDays());
                userVacation.setPaternityLeave(userVacation.getPaternityLeave() - application.getDays());
            case LeaveConstant.SICK_LEAVE_NORMAL:
                userVacation.setSlnBackup(userVacation.getSlnBackup() - application.getDays());
                userVacation.setSickLeaveNormal(userVacation.getSickLeaveNormal() - application.getDays());
                break;
            default:
        }
        saveUserVacartion(userVacation);
        return previousDays;
    }

    @Override
    public void removeVacationCache(Application application) {
        //清除假期预扣部分
        UserVacation userVacation = findByUserId(application.getUserId(), LocalDate.now().getYear());

        switch (application.getLeaveType()) {
            case LeaveConstant.ANNUAL_LEAVE:
                userVacation.setAlBackup(userVacation.getAlBackup() - application.getDays());
                break;
            case LeaveConstant.SICK_LEAVE:
                userVacation.setSlBackup(userVacation.getSlBackup() - application.getDays());
                break;
            case LeaveConstant.CASUAL_LEAVE:
                userVacation.setClBackup(userVacation.getClBackup() - application.getDays());
                break;
            case LeaveConstant.MARRIAGE_LEAVE:
                userVacation.setMlBackup(userVacation.getMlBackup() - application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_1:
                userVacation.setMnlBackup(userVacation.getMnlBackup() - application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_2:
                userVacation.setMnlBackup(userVacation.getMnlBackup() - application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_3:
                userVacation.setMnlBackup(userVacation.getMnlBackup() - application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_4:
                userVacation.setMnlBackup(userVacation.getMnlBackup() - application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_5:
                userVacation.setMnlBackup(userVacation.getMnlBackup() - application.getDays());
                break;
            case LeaveConstant.FUNERAL_LEAVE:
                userVacation.setFlBackup(userVacation.getFlBackup() - application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE:
                userVacation.setMnlBackup(userVacation.getMnlBackup() - application.getDays());
                break;
            case LeaveConstant.PATERNITY_LEAVE:
                userVacation.setPnlBackup(userVacation.getPnlBackup() - application.getDays());
                break;
            case LeaveConstant.SICK_LEAVE_NORMAL:
                userVacation.setSlnBackup(userVacation.getSlnBackup() - application.getDays());
                break;
            default:
        }
        saveUserVacartion(userVacation);
    }

    @Override
    synchronized  public void cancelVacationCache(Application application) {
        if(ApplicationStatusEnum.FINISH2CANCEL.toString().equals(application.getStatus())) {
            //finish2cancel
            rollBackUserVacation(application);
        }else if(ApplicationStatusEnum.RUNNING2CANCEL.toString().equals(application.getStatus())) {
            //running2cancel
            removeVacationCache(application);
        }
    }

    @Override
    public UserVacationVO vacation2VO(UserVacation vacation, int year) {
        if (vacation != null) {
            UserVacationVO userVacationVO = UserVacationVO.builder()
                    .annualLeave(vacation.getAnnualLeave())
                    .casualLeave(vacation.getCasualLeave())
                    .funeralLeave(vacation.getFuneralLeave())
                    .marriageLeave(vacation.getMarriageLeave())
                    .maternityLeave(vacation.getMaternityLeave())
                    .paternityLeave(vacation.getPaternityLeave())
                    .sickLeave(vacation.getSickLeave())
                    .sickLeaveNormal(vacation.getSickLeaveNormal())
                    .annualShould(vacation.getAnnualShould())
                    .build();
            String annualTotal = userVacationVO.getAnnualLeave() + "(" + (year) + ")";
            if (Objects.nonNull(vacation.getRestAnnualLeave())) {
                userVacationVO.setRestAnnualLeave(vacation.getRestAnnualLeave());
                annualTotal += "|" + vacation.getRestAnnualLeave() + "(" + (year - 1) + ")";
            }
            userVacationVO.setAnnualLeaveTotal(annualTotal);
            return userVacationVO;
        }
        return null;
    }

    @Override
    public void rollBackUserVacation(Application application) {
        UserVacation userVacation = findByUserId(application.getUserId(), LocalDate.now().getYear());

        switch (application.getLeaveType()) {
            case LeaveConstant.ANNUAL_LEAVE:
                userVacation.setAnnualLeave(userVacation.getAnnualLeave() + application.getDays());
                break;
            case LeaveConstant.CASUAL_LEAVE:
                userVacation.setCasualLeave(userVacation.getCasualLeave() + application.getDays());
                break;
            case LeaveConstant.SICK_LEAVE:
                userVacation.setSickLeave(userVacation.getSickLeave() + application.getDays());
                break;
            case LeaveConstant.FUNERAL_LEAVE:
                userVacation.setFuneralLeave(userVacation.getFuneralLeave() + application.getDays());
                break;
            case LeaveConstant.MARRIAGE_LEAVE:
                userVacation.setMarriageLeave(userVacation.getMarriageLeave() + application.getDays());
                break;
            case LeaveConstant.SICK_LEAVE_NORMAL:
                userVacation.setSickLeaveNormal(userVacation.getSickLeaveNormal() + application.getDays());
                break;
            case LeaveConstant.PATERNITY_LEAVE:
                userVacation.setPaternityLeave(userVacation.getPaternityLeave() + application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE:
                userVacation.setMaternityLeave(userVacation.getMaternityLeave() + application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_1:
                userVacation.setMaternityLeave(userVacation.getMaternityLeave() + application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_2:
                userVacation.setMaternityLeave(userVacation.getMaternityLeave() + application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_3:
                userVacation.setMaternityLeave(userVacation.getMaternityLeave() + application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_4:
                userVacation.setMaternityLeave(userVacation.getMaternityLeave() + application.getDays());
                break;
            case LeaveConstant.MATERNITY_LEAVE_5:
                userVacation.setMaternityLeave(userVacation.getMaternityLeave() + application.getDays());
                break;
            default:
                break;
        }
        userVacation.setTotal(userVacation.getTotal() + application.getDays());

        saveUserVacartion(userVacation);

    }

    @Override
    public void cancelUserVacation(ApplicationModifyDTO dto) {
        UserVacation userVacation = findByUserId(dto.getUserId(), LocalDate.now().getYear());

        String methodOld = "";
        String setMethodOld = "";
        String method = "";
        String setMethod = "";
        if (ApplicationStatusEnum.RUNNING.toString().equals(dto.getStatus())) {
            methodOld = LeaveTypeEnum.getSubMethodByNameForGet(dto.getLeaveTypeOld());
            setMethodOld = LeaveTypeEnum.getSubMethodByNameForSet(dto.getLeaveTypeOld());
            method = LeaveTypeEnum.getSubMethodByNameForGet(dto.getLeaveType());
            setMethod = LeaveTypeEnum.getSubMethodByNameForSet(dto.getLeaveType());
        } else if (ApplicationStatusEnum.FINISHED.toString().equals(dto.getStatus())) {
            methodOld = LeaveTypeEnum.getMethodByNameForGet(dto.getLeaveTypeOld());
            setMethodOld = LeaveTypeEnum.getMethodByNameForSet(dto.getLeaveTypeOld());
            method = LeaveTypeEnum.getMethodByNameForGet(dto.getLeaveType());
            setMethod = LeaveTypeEnum.getMethodByNameForSet(dto.getLeaveType());
        }

        InvokeStrategyContext context = new InvokeStrategyContext();
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            Double resultOld = context.getByColumn("UserVacation", dto.getLeaveTypeOld(), methodOld, userVacation);
            if (ApplicationStatusEnum.RUNNING.toString().equals(dto.getStatus())) {
                resultOld -= dto.getDaysOld();
                context.setByColumn("UserVacation", dto.getLeaveTypeOld(), setMethodOld, resultOld, userVacation);

                Double result = context.getByColumn("UserVacation", dto.getLeaveType(), method, userVacation);
                result += dto.getDays();
                context.setByColumn("UserVacation", dto.getLeaveType(), setMethod, result, userVacation);

            } else if (ApplicationStatusEnum.FINISHED.toString().equals(dto.getStatus())) {
                resultOld += dto.getDaysOld();
                context.setByColumn("UserVacation", dto.getLeaveTypeOld(), setMethodOld, resultOld, userVacation);

                Double result = context.getByColumn("UserVacation", dto.getLeaveType(), method, userVacation);
                result -= dto.getDays();
                context.setByColumn("UserVacation", dto.getLeaveType(), setMethod, result, userVacation);
            }
        } catch (Exception e) {
            lock.unlock();
            log.error("更新uservation出现异常");
        } finally {
            lock.unlock();
        }
        saveUserVacartion(userVacation);
    }
}
