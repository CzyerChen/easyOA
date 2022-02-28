package easyoa.rulemanager.task;

import easyoa.rulemanager.service.VacationService;
import easyoa.common.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Created by claire on 2019-07-08 - 19:57
 **/
@Slf4j
@Component
public class CalendarTask {
    @Autowired
    private VacationService vacationService;

    /**
     * 获取每天的数据
     */
    //@Scheduled(cron = "0 0 1 * * ?")
    public void getDailyInfo() {
        try {
            String date = DateUtil.formatLocalDateWithHyphen(LocalDate.now());
            vacationService.getDailyInfo(date);
        }catch (Exception e){
           log.error("meet exception when getting data from juhe");
        }

    }

    /**
     * 每月获取节假日
     */


    /**
     * 每年获取全年假期
     */

}
