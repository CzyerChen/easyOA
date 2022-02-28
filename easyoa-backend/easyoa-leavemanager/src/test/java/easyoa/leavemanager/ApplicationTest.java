package easyoa.leavemanager;

import easyoa.common.utils.DateUtil;
import easyoa.leavemanager.domain.GlobalFlow;
import easyoa.leavemanager.domain.biz.Application;
import easyoa.leavemanager.repository.biz.GlobalFlowRepository;
import easyoa.leavemanager.service.ApplicationService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-07-15 - 17:20
 **/
public class ApplicationTest extends AbstractApplicationTest {
    @Autowired
    private GlobalFlowRepository globalFlowRepository;
    @Autowired
    private ApplicationService applicationService;

    @Test
    public void test() {
        LocalDateTime realStart = null;
        LocalDateTime realEnd = null;
        LocalDateTime start = LocalDateTime.of(2019, 3, 10, 9, 30);
        int year = start.getYear();
        LocalDateTime end = LocalDateTime.of(2019, 3, 10, 12, 30);

        //9 -12  13：30 -18：30
           /* 3-10 9:00  -- 3-12 10:00 2.5
                3.10 00:00   3-12 12:00  5/2=2.5天
            3-10 9:00  -- 3-12 14:00 3
                3.10 00：00   3.12  24：00 3天
            3-10 14：00  -- 3-12 10：00
                3.10 12：00     3.12 12：00 2天
            3-10 14：00 --  3.12 14：00
                 3.10 12：00  3.12 24：00 2.5天

                3.10 9：00  3.12 18：30
                    3.10 00：00  3.12 24：00 3天*/

        if (start.isAfter(LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 8, 59)) &&
                start.isBefore(LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 12, 01))) {
            realStart = LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 0, 0);
        } else if (start.isAfter(LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 13, 29)) &&
                start.isBefore(LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 18, 31))) {
            realStart = LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 12, 00);
        }
        if (end.isBefore(LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 12, 1)) &&
                end.isAfter(LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 8, 59))) {
            realEnd = LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 12, 0);
        } else if (end.isBefore(LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 18, 31)) &&
                end.isAfter(LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 13, 29))) {
            realEnd = LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth()+1, 00, 00);
        }

        Duration between = Duration.between(realStart, realEnd);
        double result = between.toHours()*1.0/ 24;
        long days = between.toDays();
    }

    @Test
    public void testSearch(){
        List<GlobalFlow> list = globalFlowRepository.findByRootAndDeletedAndAssigneeIdsNotLike(1, false,"%" + 100 + "%");
        List<GlobalFlow> flows = list.stream().sorted(Comparator.comparingInt(GlobalFlow::getId)).collect(Collectors.toList());
        Assert.assertNotNull(list);
    }

    @Test
    public void testTime(){
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime startOfMonth = startOfDay.with(TemporalAdjusters.firstDayOfMonth());
        Date from = DateUtil.localDateTimeConvertToDate(startOfMonth);
    }

    @Test
    public void testEffectiveApplications(){
        List<Application> list = applicationService.findMonthlyEffectiveApplications(9L);
    }

    @Test
    public void calForVacationDays(){
        LocalDateTime start = LocalDateTime.of(2020, 4, 3, 13, 30);
        LocalDateTime end = LocalDateTime.of(2020, 4, 7, 12, 0);
        Double days = applicationService.calculateForVacation(start,end);
        Assert.assertNotEquals(0.0,days);
    }
}

