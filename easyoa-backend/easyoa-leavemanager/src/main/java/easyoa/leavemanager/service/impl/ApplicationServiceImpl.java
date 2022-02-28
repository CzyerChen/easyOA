package easyoa.leavemanager.service.impl;

import easyoa.common.constant.ApplicationStatusEnum;
import easyoa.common.constant.FlowTypeEnum;
import easyoa.common.constant.LeaveConstant;
import easyoa.common.domain.vo.ApplicationForm;
import easyoa.common.domain.vo.ApplicationSearch;
import easyoa.common.domain.vo.ApplyVO;
import easyoa.common.domain.vo.CalendarVO;
import easyoa.common.model.WeekData;
import easyoa.common.model.message.ApplyMessage;
import easyoa.common.model.message.UserMessage;
import easyoa.common.utils.DateUtil;
import easyoa.core.domain.po.GlobalVacationDetail;
import easyoa.core.domain.po.user.Department;
import easyoa.core.domain.po.user.User;
import easyoa.core.domain.po.user.UserDetail;
import easyoa.core.service.DepartmentService;
import easyoa.core.service.UserService;
import easyoa.core.service.VacationService;
import easyoa.leavemanager.domain.biz.Application;
import easyoa.leavemanager.domain.biz.ApplicationFlow;
import easyoa.leavemanager.domain.dto.ApplicationModifyDTO;
import easyoa.leavemanager.domain.user.UserPosition;
import easyoa.leavemanager.domain.user.UserVacation;
import easyoa.leavemanager.domain.vo.ApplicationExcel;
import easyoa.leavemanager.domain.vo.ApplicationVO;
import easyoa.leavemanager.repository.biz.ApplicationRepository;
import easyoa.leavemanager.runner.system.MailServer;
import easyoa.leavemanager.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-07-10 - 18:19
 **/
@Slf4j
@Service(value = "applicationService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserPositionService userPositionService;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private VacationService vacationService;
    @Autowired
    private ApplicationFlowService applicationFlowService;
    @Autowired
    private UserVacationService userVacationService;
    @Autowired
    private LeaveFileService leaveFileService;
    @Autowired
    private UserVacationCalService userVacationCalService;
    @Autowired
    private MailServer mailServer;
    @Autowired
    private VacationDetailService vacationDetailService;

    @Override
    public UserMessage<ApplyMessage> initApplyMessage(ApplyVO applyVo) {
        UserMessage<ApplyMessage> userMessage = new UserMessage<>();

        userMessage.setUserId(applyVo.getUserId());
        ApplyMessage applyMessage = new ApplyMessage();
        applyMessage.setLeaveDate(applyVo.getLeaveDateFrom());
        Period period = Period.between(DateUtil.parseLocalDate(applyVo.getLeaveDateTo()), DateUtil.parseLocalDate(applyVo.getLeaveDateFrom()));
        applyMessage.setDays(period.getDays() + 1.0);
        applyMessage.setType(String.valueOf(applyVo.getLeaveType()));
        applyMessage.setName(applyVo.getLeaveName());
        return userMessage;
    }

    @Override
    public Application saveApply(ApplicationForm applicationForm, Long fileId) {
        Application application = new Application();

        application.setApplicateType(LeaveConstant.APPLY_LEAVE);
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(application.getCreateTime());
        application.setPreviousDays(0.0);

        if (null != applicationForm.getUserId()) {
            Long userId = applicationForm.getUserId();
            application.setUserId(userId);
            User user = userService.getUser(userId);
            if (user != null) {
                application.setUserName(user.getUserName());
                application.setDeptId(user.getDeptId());
                application.setCompanyId(user.getCompanyId());
                Department dept = departmentService.findByDeptId(user.getDeptId());
                if (dept != null) {
                    application.setDeptName(dept.getDeptName());
                }
                UserPosition position = userPositionService.findByUserCode(user.getUserCode());
                if (position != null) {
                    application.setPosition(position.getName());
                }
            }
        }

        if (StringUtils.isNotBlank(applicationForm.getLeaveType())) {
            application.setLeaveType(applicationForm.getLeaveType());
        }
        if (StringUtils.isNotBlank(applicationForm.getLeaveReason())) {
            application.setLeaveReason(applicationForm.getLeaveReason());
            application.setRemark(applicationForm.getLeaveReason());
        }

        if (StringUtils.isNotBlank(applicationForm.getLeaveTimeFrom()) && StringUtils.isNotBlank(applicationForm.getLeaveTimeTo())) {
            LocalDateTime start = DateUtil.format2LocalDateTimeWithPattern(applicationForm.getLeaveTimeFrom(), DateUtil.DATE_PATTERN_HOUR_MINUTE);
            LocalDateTime end = DateUtil.format2LocalDateTimeWithPattern(applicationForm.getLeaveTimeTo(), DateUtil.DATE_PATTERN_HOUR_MINUTE);
            application.setStartTime(start);
            application.setEndTime(end);
            //出去节假日的操作
            Double forVacation = calculateForVacation(start, end);
            if (null != forVacation) {
                application.setDays(forVacation);
            }
        }
        if (null != fileId) {
            application.setResources(fileId);
        }

        application.setStatus(ApplicationStatusEnum.READY.toString());
        application.setStage("暂无阶段信息");

        Application application1 = applicationRepository.save(application);
        if (application1 != null) {
            //处理假期日期的预修改
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(LocalDate.now().getYear(),1,1);
//            Date from = calendar.getTime();

            UserVacation userVacation = userVacationService.findByUserId(application1.getUserId(), LocalDate.now().getYear());
            switch (application1.getLeaveType()) {
                case LeaveConstant.ANNUAL_LEAVE:
                    userVacation.setAlBackup(userVacation.getAlBackup() + application1.getDays());
                    break;
                case LeaveConstant.SICK_LEAVE:
                    userVacation.setSlBackup(userVacation.getSlBackup() + application1.getDays());
                    break;
                case LeaveConstant.CASUAL_LEAVE:
                    userVacation.setClBackup(userVacation.getClBackup() + application1.getDays());
                    break;
                case LeaveConstant.MARRIAGE_LEAVE:
                    userVacation.setMlBackup(userVacation.getMlBackup() + application1.getDays());
                    break;
                case LeaveConstant.MATERNITY_LEAVE_1:
                    userVacation.setMnlBackup(userVacation.getMnlBackup() + application1.getDays());
                    break;
                case LeaveConstant.MATERNITY_LEAVE_2:
                    userVacation.setMnlBackup(userVacation.getMnlBackup() + application1.getDays());
                    break;
                case LeaveConstant.MATERNITY_LEAVE_3:
                    userVacation.setMnlBackup(userVacation.getMnlBackup() + application1.getDays());
                    break;
                case LeaveConstant.MATERNITY_LEAVE_4:
                    userVacation.setMnlBackup(userVacation.getMnlBackup() + application1.getDays());
                    break;
                case LeaveConstant.MATERNITY_LEAVE_5:
                    userVacation.setMnlBackup(userVacation.getMnlBackup() + application1.getDays());
                    break;
                case LeaveConstant.FUNERAL_LEAVE:
                    userVacation.setFlBackup(userVacation.getFlBackup() + application1.getDays());
                    break;
                case LeaveConstant.MATERNITY_LEAVE:
                    userVacation.setMnlBackup(userVacation.getMnlBackup() + application1.getDays());
                    break;
                case LeaveConstant.PATERNITY_LEAVE:
                    userVacation.setPnlBackup(userVacation.getPnlBackup() + application1.getDays());
                case LeaveConstant.SICK_LEAVE_NORMAL:
                    userVacation.setSlnBackup(userVacation.getSlnBackup() + application1.getDays());
                    break;
                default:
            }
            userVacationService.saveUserVacartion(userVacation);
            updateApplication(application1);
        }
        return application1;
    }

    @Override
    public Double calculateForVacation(LocalDateTime start, LocalDateTime end) {
        List<GlobalVacationDetail> allVacations = vacationDetailService.findAllVacations(start.getYear());
        List<String> vacationStrList = allVacations.stream().map(GlobalVacationDetail::getOffday).collect(Collectors.toList());
        Double days = DateUtil.calculateDays(start, end);
        days = null != days ? days : 0.0;

        LocalDateTime startTime = start;
        while (startTime.isBefore(end)) {
            String vacationStr = DateUtil.formatFullTime(startTime, DateUtil.DATE_PATTERN_WITH_HYPHEN);
            if (startTime.isBefore(LocalDateTime.of(startTime.toLocalDate(), LocalTime.of(12, 1)))) {
                startTime = startTime.plusDays(1);
                if (vacationStrList.contains(vacationStr)) {
                    days -= 1;
                }
            } else {
                LocalDateTime tmpDate = startTime.plusDays(1);
                startTime = LocalDateTime.of(tmpDate.getYear(), tmpDate.getMonth(), tmpDate.getDayOfMonth(), 8, 30);
                if (vacationStrList.contains(vacationStr)) {
                    days -= 0.5;
                }
            }
        }
        return days < 0 ? 0 : days;
    }

    @Override
    public List<Application> findUserUnfinishedApplications(Long userId) {
        return applicationRepository.findByUserIdAndStatusAndApplicateTypeOrderByCreateTime(userId, "RUNNING", "请假");
    }

    @Override
    public List<CalendarVO> getUserApplicationsByDay(Long userId, LocalDate day) {
        List<CalendarVO> calendars = new ArrayList<>();
        if(day.isAfter(LocalDate.now())){
            return calendars;
        }
        List<Application> applications = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndStartTimeBeforeAndEndTimeAfter(userId, "FINISHED", "请假", day.plusDays(1).atStartOfDay(), day.atStartOfDay());

        if(!CollectionUtils.isEmpty(applications)){
//            String type = "success";
//            if (applications.size() > 1L) {
//                type = "warning";
//            }
            CalendarVO dto = new CalendarVO();
            dto.setDay(DateUtil.formatLocalDateWithHyphen(day));
            dto.setHit(applications.size());
            calendars.add(dto);
        }
        return calendars;
    }

    @Override
    public List<CalendarVO> getUserApplicationsByMonth(Long userId, LocalDate day) {
        List<CalendarVO> calendars = new ArrayList<>();
        if(day.isAfter(LocalDate.now())){
            return calendars;
        }
        LocalDate firstDay = day.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = day.with(TemporalAdjusters.lastDayOfMonth());
        List<Application> applications = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndStartTimeBeforeAndEndTimeAfter(userId, "FINISHED", "请假", lastDay.plusDays(1).atStartOfDay(), firstDay.atStartOfDay());
        if(!CollectionUtils.isEmpty(applications)){
            String type = "success";
            if (applications.size() > 1L) {
                type = "warning";
            }
//            calendars.add(new CalendarVO(type));
        }
        return calendars;
    }

    public List<CalendarVO> getUserApplicationsByTimeBetween(Long userId, LocalDate from, LocalDate to) {
        List<CalendarVO> list = new ArrayList<>();
        List<Application> applications = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndCreateTimeBetweenOrderByCreateTime(userId, "FINISHED", "请假", from.atStartOfDay(), to.atStartOfDay());
        if (!CollectionUtils.isEmpty(applications)) {
            List<String> days = applications.stream().map(d -> DateUtil.daysFormatBetween(d.getStartTime(), d.getEndTime(), DateUtil.DATE_PATTERN_WITH_HYPHEN)).flatMap(Collection::stream).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(days)) {
              days.stream()
                        .collect(Collectors.groupingBy(String::valueOf, Collectors.counting()))
                        .entrySet().stream().map(entry -> {
                            String type = "success";
                            if (entry.getValue() > 1L) {
                                type = "warning";
                            }
                            return "null";
                        }).collect(Collectors.toList());
            }
        }
        return null;
    }

    @Override
    public void updateApplication(Application application) {
        application.setUpdateTime(LocalDateTime.now());
        applicationRepository.save(application);
    }

    @Override
    public List<ApplicationVO> application2VOs(List<Application> list) {
        if (list != null && list.size() != 0) {
            List<Application> sortedList = list.stream().sorted(Comparator.comparing(Application::getCreateTime).reversed()).collect(Collectors.toList());
            return sortedList.stream().map(a -> {
                ApplicationVO vo = new ApplicationVO();

                vo.setApplicationId(a.getId());
                vo.setUserId(a.getUserId());
                vo.setUserName(a.getUserName());
                vo.setLeaveType(a.getLeaveType());
                vo.setLeaveReason(a.getLeaveReason());
                vo.setStartTime(DateUtil.formatFullTime(a.getStartTime(), DateUtil.DATE_PATTERN_HOUR_MINUTE));
                vo.setEndTime(DateUtil.formatFullTime(a.getEndTime(), DateUtil.DATE_PATTERN_HOUR_MINUTE));
                vo.setPosition(a.getPosition());
                vo.setRemark(a.getRemark());
                vo.setStatus(a.getStatus());
                vo.setDeptId(a.getDeptId());
                vo.setDeptName(a.getDeptName());
                vo.setDays(a.getDays());
                vo.setStage(a.getStage());
                vo.setCreateTime(DateUtil.formatFullTime(a.getCreateTime(), DateUtil.BASE_PATTERN));
                vo.setPreviousDays(a.getPreviousDays());
                vo.setCurrentDays(a.getDays() - a.getPreviousDays());
                if (null != a.getResources()) {
                    vo.setResources(String.valueOf(a.getResources()));
                    String fileName = leaveFileService.findFileOriginNameById(a.getResources());
                    if (StringUtils.isNotBlank(fileName)) {
                        vo.setFileName(fileName);
                    } else {
                        vo.setFileName("hello_world.txt");
                    }
                }
                return vo;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<ApplicationExcel> application2Excel(List<Application> list) {
        List<ApplicationExcel> excelList = new ArrayList<>();
        if (list != null && list.size() != 0) {
            list.forEach(a -> {
                UserDetail user = userService.findUserDetailByUserId(a.getUserId());
                if (user != null) {
                    ApplicationExcel vo = new ApplicationExcel();
                    vo.setApplicationId(a.getId());
                    vo.setUserName(user.getChineseName());
                    vo.setUserCode(user.getId());
                    vo.setLeaveType(a.getLeaveType());
                    vo.setLeaveReason(a.getLeaveReason());
                    vo.setStartTime(DateUtil.formatFullTime(a.getStartTime(), DateUtil.DATE_PATTERN_HOUR_MINUTE));
                    vo.setEndTime(DateUtil.formatFullTime(a.getEndTime(), DateUtil.DATE_PATTERN_HOUR_MINUTE));
                    vo.setRemark(a.getRemark());
                    vo.setStatus(a.getStatus());
                    vo.setDeptName(a.getDeptName());
                    vo.setDays(a.getDays());
                    vo.setPreviousDays(a.getPreviousDays());
                    vo.setCurrentDays(a.getDays() - a.getPreviousDays());
                    vo.setStage(a.getStage());
                    vo.setCreateTime(DateUtil.formatFullTime(a.getCreateTime(), DateUtil.BASE_PATTERN));
                    excelList.add(vo);
                }
            });
        }
        return excelList;
    }

    @Override
    public Page<Application> findPageUserApplications(Long userId, Pageable pageable) {
        return applicationRepository.findByUserIdAndApplicateTypeOrderByCreateTimeDesc(userId, "请假", pageable);
    }

    @Override
    public List<Application> findAllUserApplication(Long userId) {
        return applicationRepository.findByUserIdAndApplicateType(userId, "请假");
    }

    @Override
    public void terminateAllApplications(List<Long> idList) {
        List<Application> list = applicationRepository.findAllById(idList);
        list.forEach(a -> {
            a.setStatus(ApplicationStatusEnum.TERMINATED.toString());
        });
        applicationRepository.saveAll(list);
    }

    @Override
    public Application findById(Long applicationId) {
        return applicationRepository.findById(applicationId).orElse(null);
    }

    @Override
    public List<Application> findAssignedApplicationsTodo(Long assignee) {
        List<ApplicationFlow> flows = applicationFlowService.findByAssigneeTodo(assignee);
        List<Long> applicationIds = flows.stream().map(ApplicationFlow::getApplicationId).distinct().collect(Collectors.toList());

        return applicationRepository.findByStatusInAndIdIn(Collections.singletonList(ApplicationStatusEnum.RUNNING.toString()),applicationIds);
    }

    @Override
    public List<Application> findAssignedApplicationsDone(Long assignee) {
        List<ApplicationFlow> flows = applicationFlowService.findByAssigneeDone(assignee);
        List<Long> applicationIds = flows.stream().map(ApplicationFlow::getApplicationId).distinct().collect(Collectors.toList());
        return applicationRepository.findAllById(applicationIds);
    }

    @Override
    public Page<Application> findBySearchParam(ApplicationSearch applicationSearch, Pageable pageable) {
        if (StringUtils.isNotBlank(applicationSearch.getUserCode())) {
            User byCode = userService.getUserByCode(applicationSearch.getUserCode());
            if (byCode != null) {
                applicationSearch.setUserId(byCode.getUserId());
            }
        }
        return applicationRepository.findPageApplicationWithSearchParam(applicationSearch, pageable);
    }

    @Override
    public List<Application> findMonthlyEffectiveApplications(Long userId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        Month month = now.getMonth();
        LocalDate firstDay = DateUtil.getFirstDay(year, month.getValue());
        LocalDate endDay = DateUtil.getEndDay(year, month.getValue());
        LocalDateTime start = LocalDateTime.of(firstDay, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDay, LocalTime.MAX);
        List<Application> all = new ArrayList<>();
        //正在进行的申请
        List<Application> running = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndCreateTimeBetweenOrderByCreateTime(userId, "RUNNING", "请假", start, end);
        if (running != null && running.size() != 0) {
            all.addAll(running);
        }
        List<Application> finished = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndCreateTimeBetweenOrderByCreateTime(userId, "FINISHED", "请假", start, end);
        if (finished != null && finished.size() != 0) {
            all.addAll(finished);
        }

        return all;
    }

    @Override
    public Integer findWeeklyEffectiveApplicationsCount(Long userId) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        Map<Integer, WeekData> weeks = DateUtil.weeks(YearMonth.now());
        for (Map.Entry<Integer, WeekData> entry : weeks.entrySet()) {
            WeekData value = entry.getValue();
            if ((LocalDate.now().equals(value.getStart()) || LocalDate.now().isAfter(value.getStart())) && (LocalDate.now().isBefore(value.getEnd()) || LocalDate.now().equals(value.getEnd()))) {
                start = LocalDateTime.of(value.getStart(), LocalTime.MIN);
                end = LocalDateTime.of(value.getEnd(), LocalTime.MAX);
            }
        }
        int count = 0;
        if (start != null && end != null) {
            List<Application> running = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndCreateTimeBetweenOrderByCreateTime(userId, "RUNNING", "请假", start, end);
            if (running != null) {
                count += running.size();
            }
            List<Application> finished = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndCreateTimeBetweenOrderByCreateTime(userId, "FINISHED", "请假", start, end);
            if (finished != null) {
                count += finished.size();
            }

        }
        return count;
    }

    @Override
    public Map<Long, String> findApplicationNameByIds(List<Long> ids) {
        List<Application> apps = applicationRepository.findAllById(ids);
        if (apps != null && apps.size() != 0) {
            return apps.stream().collect(Collectors.toMap(Application::getId, Application::getLeaveType));
        } else {
            return null;
        }
    }

    @Override
    public ApplicationModifyDTO updateApplication(ApplicationVO applicationVO) {
        Application application = findById(applicationVO.getApplicationId());
        if (application != null) {
            LocalDate date = LocalDate.of(
                    application.getStartTime().getYear(),
                    application.getStartTime().getMonth(),
                    1);
            ApplicationModifyDTO dto = ApplicationModifyDTO.builder()
                    .userId(application.getUserId())
                    .userName(application.getUserName())
                    .applicationId(application.getId())
                    .month(date)
                    .leaveTypeOld(application.getLeaveType())
                    .leaveType(applicationVO.getLeaveType())
                    .daysOld(application.getDays())
                    .days(applicationVO.getDays())
                    .status(application.getStatus())
                    .build();

            if (null != applicationVO.getDays()) {
                application.setDays(applicationVO.getDays());
            }
            if (!StringUtils.equals(application.getLeaveType(), applicationVO.getLeaveType())) {
                application.setLeaveType(applicationVO.getLeaveType());
            }
            if (StringUtils.isNotBlank(applicationVO.getStartTime())) {
                application.setStartTime(DateUtil.format2LocalDateTime(applicationVO.getStartTime()));
            }
            if (StringUtils.isNotBlank(applicationVO.getEndTime())) {
                application.setEndTime(DateUtil.format2LocalDateTime(applicationVO.getEndTime()));
            }
            application.setUpdateTime(LocalDateTime.now());
            applicationRepository.save(application);

            return dto;
        }
        return null;
    }

    @Override
    public void rewriteApplicationCal(ApplicationModifyDTO dto) throws MessagingException, UnsupportedEncodingException {
        //对于uservacation 只需要将数据进行调整
        userVacationService.cancelUserVacation(dto);
        //对于uservacationcal 暂且以starttime作为月份进行修改
        userVacationCalService.cancelUserVacationCal(dto);
        //把最后的流程置为完成
        /*ApplicationFlow applicationFlow = applicationFlowService.findByApplicationId(dto.getApplicationId());
        applicationFlow.setStatus(true);
        applicationFlowService.saveApplicationFlow(applicationFlow);*/
        //添加变更流程
        ApplicationFlow flow = new ApplicationFlow();
        flow.setApplicationId(dto.getApplicationId());
        flow.setCreateTime(LocalDateTime.now());
        flow.setName("流程变更");
        flow.setType(FlowTypeEnum.OVER.getId());
        flow.setUserId(dto.getUserId());
        flow.setContent("您的申请流程变更：原请假类型："
                + dto.getLeaveTypeOld() + " 时长：" + dto.getDaysOld()
                + "; 新请假类型：" + dto.getLeaveType() + " 时长: " + dto.getDays());
        applicationFlowService.saveApplicationFlow(flow);
        //发送消息
        mailServer.sendSimpleMail(dto.getUserName(), "系统：申请流程进度通知", flow.getContent());
        mailServer.sendSimpleMail(dto.getUserName(), "系统：申请流程进度通知", flow.getContent());

    }

}
