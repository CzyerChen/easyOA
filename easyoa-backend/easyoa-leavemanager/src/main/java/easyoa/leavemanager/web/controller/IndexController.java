package easyoa.leavemanager.web.controller;

import easyoa.leavemanager.web.AbstractController;
import easyoa.common.domain.ApiResponse;
import easyoa.common.domain.vo.ApplicationOverall;
import easyoa.common.domain.vo.CalendarVO;
import easyoa.common.domain.vo.NoticeVO;
import easyoa.common.utils.DateUtil;
import easyoa.leavemanager.domain.biz.Application;
import easyoa.leavemanager.domain.user.UserNotice;
import easyoa.leavemanager.domain.user.UserVacation;
import easyoa.leavemanager.service.ApplicationService;
import easyoa.leavemanager.service.UserNoticeService;
import easyoa.leavemanager.service.UserVacationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Created by claire on 2019-06-26 - 16:22
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/index")
@Api(value = "首页数据相关接口",description = "首页数据相关接口")
public class IndexController extends AbstractController {
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private UserVacationService userVacationService;
    @Autowired
    private UserNoticeService userNoticeService;


    /**
     *  用户首页数据
     * @param userId
     * @return
     */
    @ApiOperation(value = "用户首页统计数据")
    @GetMapping("/{userId}")
    public ApiResponse index(@NotBlank(message = "{required}") @PathVariable String userId) {
        Map<String, Object> map = new HashMap<>();

        List<Application> applications = applicationService.findMonthlyEffectiveApplications(Long.valueOf(userId));
        UserVacation userVacation = userVacationService.findByUserId(Long.valueOf(userId), LocalDate.now().getYear());
        if (Objects.isNull(userVacation) || Objects.isNull( userVacation.getRestAnnualLeave())) {
            map.put("month", 0);
            map.put("week", 0);
            map.put("rest", 0);
        } else {
            String rest = "";
            map.put("month", userVacation.getMonthlyApply());
            map.put("week", userVacation.getWeeklyApply());

            if(Objects.nonNull(userVacation)){
                rest =  rest + userVacation.getAnnualLeave()+"("+LocalDate.now().getYear()+")";
            }
            if(Objects.nonNull(userVacation.getRestAnnualLeave())) {
                if(StringUtils.isNotBlank(rest)){
                    rest += "|";
                }
                rest = rest+ userVacation.getRestAnnualLeave()+"("+(LocalDate.now().getYear()-1)+")";
            }
            map.put("rest", rest);
        }
        List<ApplicationOverall> overalls = buildOverallContent(applications);
        if (overalls == null) {
            map.put("rows", Collections.emptyList());
        } else {
            map.put("rows", overalls);
        }

        return ApiResponse.builder().data(map).build();
    }

    @ApiOperation(value = "用户首页待办申请记录数据")
    @GetMapping("/applyinfo/{userId}")
    public ApiResponse applyInfo(@PathVariable Long userId) {
        List<Application> applys = applicationService.findUserUnfinishedApplications(userId);
        return ApiResponse.success(applicationService.application2VOs(applys));
    }

    @ApiOperation(value = "用户首页待办处理记录数据")
    @GetMapping("/applyprocessinfo/{userId}")
    public ApiResponse applyProcessInfo( @PathVariable Long userId) {
        List<Application> assignedApplications = applicationService.findAssignedApplicationsTodo(userId);
        return ApiResponse.success(applicationService.application2VOs(assignedApplications));
    }

        @ApiOperation(value = "用户首页申请进度数据")
    @GetMapping("/applymsg/{userId}")
    public ApiResponse applyMsg(@NotBlank(message = "{required}") @PathVariable String userId) {
        List<NoticeVO> list = new ArrayList<>();
        List<UserNotice> userNotices = userNoticeService.findUserNoticeByUserIdAndTimeBetween(Long.valueOf(userId), LocalDate.now().minusDays(15), LocalDate.now());
        if(!CollectionUtils.isEmpty(userNotices)){
            list = userNoticeService.notice2Vo(userNotices);
        }
        return ApiResponse.success(list);
    }

    @ApiOperation(value = "用户首页休假分日数据")
    @GetMapping("/applydays/{userId}")
    public ApiResponse applyDays(@NotBlank(message = "{required}") @PathVariable String userId,@RequestParam String day) {
        
        List<CalendarVO> calendars = applicationService.getUserApplicationsByDay(Long.valueOf(userId), DateUtil.parseLocalDate(day));
        return ApiResponse.success(calendars);
    }

    @ApiOperation(value = "用户首页休假分日数据")
    @GetMapping("/applydays/default/{userId}")
    public ApiResponse defaultApplyDays(@NotBlank(message = "{required}") @PathVariable String userId,@RequestParam String day) {
        LocalDate today = DateUtil.parseLocalDate(day);
        LocalDate firstDay = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = today.with(TemporalAdjusters.lastDayOfMonth());
        if(lastDay.isAfter(LocalDate.now())){
            lastDay = LocalDate.now();
        }
        List<CalendarVO> results = new ArrayList<>();
        while(!firstDay.isAfter(lastDay)){
            List<CalendarVO> calendars = applicationService.getUserApplicationsByDay(Long.valueOf(userId), firstDay);
            if(!CollectionUtils.isEmpty(calendars)){
                results.add(calendars.get(0));
            }
            firstDay = firstDay.plusDays(1);
        }
        return ApiResponse.success(results);
    }

    @ApiOperation(value = "用户首页休假分月数据")
    @GetMapping("/applymonths/{userId}")
    public ApiResponse applyMonths(@NotBlank(message = "{required}") @PathVariable String userId,@RequestParam String month) {
        List<CalendarVO> calendars = applicationService.getUserApplicationsByMonth(Long.valueOf(userId), DateUtil.parseLocalDate(month));
        return ApiResponse.success(calendars);
    }


    /**
     *  用户申请近况数据
     * @param list
     * @return
     */
    private List<ApplicationOverall> buildOverallContent(List<Application> list) {
        List<ApplicationOverall> overalls = new ArrayList<>();
        if (list != null && list.size() != 0) {
            list.forEach(a -> {
                Double day = a.getDays() == null ? 0 : a.getDays();
                ApplicationOverall overall = new ApplicationOverall();
                overall.setName("类型: " + a.getLeaveType() + "  状态: " + a.getStatus() + "  阶段: " + a.getStage());
                overall.setContent("请假区间: " + DateUtil.format(a.getStartTime()) + " 至 " + DateUtil.format(a.getEndTime()) +
                        "  有效天数: " + day + "  创建时间: " + DateUtil.format(a.getCreateTime()));
                overalls.add(overall);
            });
        }

        return overalls;
    }
}
