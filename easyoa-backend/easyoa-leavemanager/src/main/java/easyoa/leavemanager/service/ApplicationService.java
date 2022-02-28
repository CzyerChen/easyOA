package easyoa.leavemanager.service;

import easyoa.common.domain.vo.ApplicationForm;
import easyoa.common.domain.vo.ApplicationSearch;
import easyoa.common.domain.vo.ApplyVO;
import easyoa.common.domain.vo.CalendarVO;
import easyoa.leavemanager.domain.biz.Application;
import easyoa.leavemanager.domain.dto.ApplicationModifyDTO;
import easyoa.leavemanager.domain.vo.ApplicationExcel;
import easyoa.leavemanager.domain.vo.ApplicationVO;
import easyoa.common.model.message.ApplyMessage;
import easyoa.common.model.message.UserMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-07-10 - 18:18
 **/
public interface ApplicationService {
    UserMessage<ApplyMessage> initApplyMessage(ApplyVO applyVo);

    Application saveApply(ApplicationForm applicationForm, Long fileId);

    void updateApplication(Application application);

    List<ApplicationVO> application2VOs(List<Application> list);

    List<ApplicationExcel> application2Excel(List<Application> list);

    Page<Application>  findPageUserApplications(Long userId, Pageable pageable);

    List<Application> findAllUserApplication(Long userId);

    void terminateAllApplications(List<Long> idList);

    Application findById(Long applicationId);

    List<Application> findAssignedApplicationsTodo(Long assignee);

    List<Application> findAssignedApplicationsDone(Long assignee);

    Page<Application> findBySearchParam(ApplicationSearch applicationSearch, Pageable pageable);

    List<Application> findMonthlyEffectiveApplications(Long  userId);

    Integer findWeeklyEffectiveApplicationsCount(Long  userId);

    Map<Long,String> findApplicationNameByIds(List<Long> ids);

    ApplicationModifyDTO updateApplication(ApplicationVO applicationVO);

    void rewriteApplicationCal(ApplicationModifyDTO dto) throws MessagingException, UnsupportedEncodingException;

    Double calculateForVacation(LocalDateTime start, LocalDateTime end);

    List<Application> findUserUnfinishedApplications(Long userId);

    List<CalendarVO> getUserApplicationsByDay(Long userId, LocalDate day);

    List<CalendarVO> getUserApplicationsByMonth(Long userId, LocalDate day);

}
