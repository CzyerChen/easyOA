package easyoa.core.service;

import easyoa.core.domain.dto.VacationExcel;
import easyoa.common.domain.vo.VacationCalendar;
import easyoa.common.domain.vo.VacationVO;
import easyoa.core.domain.po.GlobalVacation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by claire on 2019-07-09 - 11:47
 **/
public interface VacationService {
     List<GlobalVacation> findAll();

     Page<GlobalVacation> findAll(Pageable pageable);

     VacationVO findVacationById(Integer vacationId);

     void saveVacation(GlobalVacation globalVacation);

     List<VacationCalendar> findMonthlyCalendarInfo(int year,int month);

     VacationCalendar findCalendarInfo(int year,int month,int day);

     void processGlobalVacation(List<VacationExcel> vacations);

}
