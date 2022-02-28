package easyoa.rulemanager.calendar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by claire on 2019-07-04 - 17:12
 **/
@Slf4j
@Component
public class CalendarServerFallback  implements CalendarServer {
    @Override
    public String getYearVacation(String year, String key) {
        log.error("请求聚合数据失败，请检查");
        return "fail to request juhe ";
    }

    @Override
    public String getRecentVacation(String yearMonth, String key) {
        log.error("请求聚合数据失败，请检查");
        return "fail to request juhe ";
    }

    @Override
    public String getDailyInfo(String date, String key) {
        log.error("请求聚合数据失败，请检查");
        return "fail to request juhe ";
    }
}
