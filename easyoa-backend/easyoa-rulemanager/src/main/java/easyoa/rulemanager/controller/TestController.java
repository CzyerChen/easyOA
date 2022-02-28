package easyoa.rulemanager.controller;

import easyoa.rulemanager.calendar.CalendarServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by claire on 2019-07-04 - 17:27
 **/
@Slf4j
@RestController
public class TestController {
    @Autowired
    private CalendarServer calendarServer;

    @GetMapping("/api/test/{year}/{key}")
    public String testRequest(@PathVariable String year, @PathVariable String key){
        return calendarServer.getYearVacation(year, key);
    }
}
