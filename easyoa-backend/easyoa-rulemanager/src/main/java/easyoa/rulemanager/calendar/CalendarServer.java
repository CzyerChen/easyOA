package easyoa.rulemanager.calendar;

import easyoa.rulemanager.config.api.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by claire on 2019-07-04 - 16:38
 **/
@FeignClient(value = "calendar-server",fallback = CalendarServerFallback.class,url = "http://v.juhe.cn",configuration= FeignConfig.class)
public interface CalendarServer {

    @RequestMapping(method = RequestMethod.GET,value = "/calendar/year")
    String getYearVacation(@RequestParam(value = "year")String year, @RequestParam(value = "key")String key);

    @RequestMapping(method = RequestMethod.GET,value = "/calendar/month")
    String getRecentVacation(@RequestParam(value = "year-month")String yearMonth,@RequestParam(value = "key")String key);

    @RequestMapping(method = RequestMethod.GET,value = "/calendar/day")
    String getDailyInfo(@RequestParam(value = "date")String date,@RequestParam(value = "key")String key);

}
