package easyoa.leavemanager;

import easyoa.common.domain.vo.VacationCalSearch;
import easyoa.common.model.WeekData;
import easyoa.common.utils.DateUtil;
import easyoa.core.domain.po.GlobalVacation;
import easyoa.core.service.VacationService;
import easyoa.leavemanager.domain.dto.ApplicationModifyDTO;
import easyoa.leavemanager.domain.dto.UserVacationCalDTO;
import easyoa.leavemanager.domain.user.UserVacationCal;
import easyoa.leavemanager.service.UserVacationCalService;
import easyoa.leavemanager.service.UserVacationService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-07-09 - 14:53
 **/
public class VacationTest  extends AbstractApplicationTest {
    @Autowired
    private VacationService vacationService;
    @Autowired
    private UserVacationCalService userVacationCalService;
    @Autowired
    private UserVacationService userVacationService;

    @Test
    public void testFindAll(){
        List<GlobalVacation> all = vacationService.findAll();
        Assert.assertNotNull(all);
    }

    @Test
    public void testWeekData(){
        Map<Integer, WeekData> weeks = DateUtil.weeks(YearMonth.now());
        for(Map.Entry<Integer,WeekData> entry: weeks.entrySet()){
            WeekData value = entry.getValue();
            if((LocalDate.now().equals(value.getStart()) ||LocalDate.now().isAfter(value.getStart())) && (LocalDate.now().isBefore(value.getEnd())||LocalDate.now().equals(value.getEnd()))){
                System.out.println(value.toString());
            }
        }
        Assert.assertNotNull(weeks);
    }


    @Test
    public void testDynamicUpdateFoUserVacation(){
        ApplicationModifyDTO dto = new ApplicationModifyDTO(1L,"","",60L,LocalDate.of(2019,8,1),"事假","年假",1.0,1.0,"RUNNING");
        userVacationService.cancelUserVacation(dto);
    }

    @Test
    public void testDynamicUpdateForUserVacationCal(){
        ApplicationModifyDTO dto = new ApplicationModifyDTO(1L,"","",60L,LocalDate.of(2019,8,1),"年假","事假",1.0,1.0,"FINISHED");
        userVacationCalService.cancelUserVacationCal(dto);
    }

    @Test
    public void testFindUserIdAndMonth(){
        UserVacationCal userVacationCal = userVacationCalService.findByUserIdAndMonth(1L, LocalDate.of(2019, 8, 1));
        Assert.assertNotNull(userVacationCal);
    }



}
