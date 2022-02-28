package easyoa.leavemanager.service.impl;

import easyoa.leavemanager.domain.dto.ApplicationModifyDTO;
import easyoa.leavemanager.domain.dto.UserVacationCalDTO;
import easyoa.leavemanager.domain.user.UserVacation;
import easyoa.leavemanager.domain.user.UserVacationCal;
import easyoa.leavemanager.handler.InvokeStrategyContext;
import easyoa.leavemanager.service.UserVacationCalService;
import easyoa.common.constant.ApplicationStatusEnum;
import easyoa.common.constant.LeaveTypeEnum;
import easyoa.common.domain.vo.VacationCalSearch;
import easyoa.common.utils.DateUtil;
import easyoa.core.repository.UserRepository;
import easyoa.leavemanager.repository.user.UserVacationCalRepository;
import easyoa.leavemanager.repository.user.UserVacationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by claire on 2019-08-07 - 09:08
 **/
@Slf4j
@Service(value = "userVacationCalService")
public class UserVacationCalServiceImpl implements UserVacationCalService {
    @Autowired
    private UserVacationCalRepository userVacationCalRepository;
    @Autowired
    private UserVacationRepository userVacationRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserVacationCal> findAll() {
        return userVacationCalRepository.findAll();
    }

    @Override
    public Page<UserVacationCalDTO> findPageUserVacationCal(VacationCalSearch vacationCalSearch, Pageable pageable) {
        List<Integer> userIds = new ArrayList<>();
        if(Objects.nonNull(vacationCalSearch.getCompanyId())){
            userIds = userRepository.findUserIdByCompanyId(vacationCalSearch.getCompanyId());
            vacationCalSearch.setUserIds(userIds);
        }
        Page<UserVacationCal> vacationCalInfo = userVacationCalRepository.findPageVacationCalInfo(vacationCalSearch, pageable);
        List<UserVacationCalDTO> list = new ArrayList<>();
        if (Objects.nonNull(vacationCalInfo) && CollectionUtils.isNotEmpty(vacationCalInfo.getContent())) {
            vacationCalInfo.getContent().forEach(vacation -> {
                int year = LocalDate.now().getYear();
                if (StringUtils.isNotBlank(vacationCalSearch.getMonth())) {
                    year = DateUtil.parseLocalDateWithAlias(vacationCalSearch.getMonth() + "-01").getYear();
                }
                UserVacation userVacation = userVacationRepository.findByUserIdAndYear(vacation.getUserId(), year);

                UserVacationCalDTO calDTO = new UserVacationCalDTO(vacation.getId(), vacation.getUserId(),
                        vacation.getUserCode(), vacation.getUserName(),
                        vacation.getHireDate(), vacation.getAnnual(),
                        vacation.getSick(),
                        vacation.getSickNormal(),
                        vacation.getMarriage(),
                        vacation.getMaterPaternity(),
                        vacation.getMaternity4(),
                        vacation.getFuneral(),
                        vacation.getCasual(),
                        vacation.getAnnualShould(),
                        vacation.getAnnualCal(),
                        vacation.getAnnualRest(),
                        vacation.getCalculateMonth(),
                        vacation.getCalculateTime(),
                        userVacation.getRestAnnualLeave());
                list.add(calDTO);
            });
        }
        return new PageImpl<>(list,vacationCalInfo.getPageable(),vacationCalInfo.getTotalElements());
    }

    @Override
    public UserVacationCal findByUserIdAndMonth(Long userId, LocalDate month) {
        LocalDate from = month.minusDays(1);
        LocalDate to = month.plusDays(2);
        return userVacationCalRepository.findByUserIdAndCalculateMonthBetween(userId, from, to);
    }

    @Override
    public void cancelUserVacationCal(ApplicationModifyDTO dto) {
        UserVacationCal userVacationCal = findByUserIdAndMonth(dto.getUserId(), dto.getMonth());
        if (userVacationCal != null && ApplicationStatusEnum.FINISHED.toString().equals(dto.getStatus())) {
            String getMethodOld = LeaveTypeEnum.getMethodByNameCalForGet(dto.getLeaveTypeOld());
            String getMethod = LeaveTypeEnum.getMethodByNameCalForGet(dto.getLeaveType());

            String setMethodOld = LeaveTypeEnum.getMethodByNameCalForSet(dto.getLeaveTypeOld());
            String setMethod = LeaveTypeEnum.getMethodByNameCalForSet(dto.getLeaveType());

            InvokeStrategyContext context = new InvokeStrategyContext();
            Lock lock = new ReentrantLock();
            lock.lock();
            try {
                Double resultOld = context.getByColumn("UserVacationCal", dto.getLeaveTypeOld(), getMethodOld, userVacationCal);
                Double result = context.getByColumn("UserVacationCal", dto.getLeaveType(), getMethod, userVacationCal);
                resultOld -= dto.getDaysOld();
                result += dto.getDays();

                context.setByColumn("UserVacationCal", dto.getLeaveTypeOld(), setMethodOld, resultOld, userVacationCal);
                context.setByColumn("UserVacationCal", dto.getLeaveType(), setMethod, result, userVacationCal);
            } catch (Exception e) {
                log.error("更新uservacationcal出现异常");
                lock.unlock();
            } finally {
                lock.unlock();
            }
            userVacationCalRepository.save(userVacationCal);
        }
    }
}
