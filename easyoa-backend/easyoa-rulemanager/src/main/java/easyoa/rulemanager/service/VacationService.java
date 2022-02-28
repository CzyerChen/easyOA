package easyoa.rulemanager.service;

import easyoa.rulemanager.domain.GlobalVacation;

import java.util.List;

/**
 * Created by claire on 2019-07-08 - 16:05
 **/
public interface VacationService {

    void getDailyInfo(String date);

    void getYearVacation(String year);

    void getRecentHoliday(String yearMonth);

    void saveVacation(List<GlobalVacation> list);

    void saveVacation(GlobalVacation vacation);

    GlobalVacation findVacationByName(String name,String startDate);

    GlobalVacation findVacationByName(String name,boolean finish);
}
