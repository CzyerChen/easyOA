package easyoa.rulemanager.service.impl;

import easyoa.rulemanager.domain.Application;
import easyoa.rulemanager.service.ApplicationService;
import easyoa.common.model.WeekData;
import easyoa.common.utils.DateUtil;
import easyoa.rulemanager.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-07-17 - 09:37
 **/
@Service(value = "applicationService")
public class ApplicationServiceImpl implements ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;


    @Override
    public List<Application> findDailyEffectiveApplications(Long userId) {
        List<Application> list = new ArrayList<>();
        LocalDateTime start= LocalDateTime.of(LocalDate.now(),LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(),LocalTime.MAX);
        List<Application> running = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndCreateTimeBetweenOrderByCreateTime(userId, "RUNNING", "请假",start, end);
        if(running != null) {
           list.addAll(running);
        }
        List<Application> finished = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndCreateTimeBetweenOrderByCreateTime(userId, "FINISHED", "请假",start, end);
        if(finished != null) {
            list.addAll(finished);
        }
        return list;
    }

    @Override
    public Application findById(Long applicationId) {
        return applicationRepository.findById(applicationId).orElse(null);
    }


    @Override
    public Integer findMonthlyEffectiveApplicationsCount(Long  userId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        Month month = now.getMonth();
        LocalDate firstDay = DateUtil.getFirstDay(year, month.getValue());
        LocalDate endDay = DateUtil.getEndDay(year, month.getValue());
        LocalDateTime start = LocalDateTime.of(firstDay, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDay,LocalTime.MAX);
        int count = 0;
        //正在进行的申请
        List<Application> running = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndCreateTimeBetweenOrderByCreateTime(userId, "RUNNING", "请假",start, end);
        if(running != null) {
           count += running.size();
        }
        List<Application> finished = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndCreateTimeBetweenOrderByCreateTime(userId, "FINISHED", "请假",start, end);
        if(finished != null) {
           count += finished.size();
        }
        return count;
    }

    @Override
    public Double findApplicationCountLastMonth(Long userId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        Month month = now.getMonth();
        Month month1 = month.minus(1);
        if(month1.getValue() > month.getValue()){
            year--;
        }
        LocalDate firstDay = DateUtil.getFirstDay(year, month1.getValue());
        LocalDate endDay = DateUtil.getEndDay(year, month1.getValue());
        LocalDateTime start = LocalDateTime.of(firstDay, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDay,LocalTime.MAX);

        List<Application> running = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndStartTimeBetween(userId, "FINISHED", "请假",start, end);
        if(running != null && running.size() != 0){
            return running.stream().mapToDouble(Application::getDays).sum();
        }
        return null;
    }

    @Override
    public Double findApplicationCount(Long userId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        Month month = now.getMonth();
        LocalDate firstDay = DateUtil.getFirstDay(year, month.getValue());
        LocalDate endDay = DateUtil.getEndDay(year, month.getValue());
        LocalDateTime start = LocalDateTime.of(firstDay, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDay,LocalTime.MAX);

        List<Application> running = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndStartTimeBetween(userId, "FINISHED", "请假",start, end);
        if(running != null && running.size() != 0){
            return running.stream().mapToDouble(Application::getDays).sum();
        }
        return 0.0;
    }

    @Override
    public List<Application> findMonthlyEffectiveApplications(Long userId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        Month month = now.getMonth();
        LocalDate firstDay = DateUtil.getFirstDay(year, month.getValue());
        LocalDate endDay = DateUtil.getEndDay(year, month.getValue());
        LocalDateTime start = LocalDateTime.of(firstDay, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDay,LocalTime.MAX);

        return applicationRepository.findByUserIdAndStatusAndApplicateTypeAndStartTimeBetween(userId, "FINISHED", "请假",start, end);
    }

    @Override
    public List<Application> findMonthlyEffectiveApplicationsWithDate(Long userId, LocalDate date) {
        int year = date.getYear();
        Month month = date.getMonth();
        LocalDate firstDay = DateUtil.getFirstDay(year, month.getValue());
        LocalDate endDay = DateUtil.getEndDay(year, month.getValue());
        LocalDateTime start = LocalDateTime.of(firstDay, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDay,LocalTime.MAX);

        return applicationRepository.findByUserIdAndStatusAndApplicateTypeAndStartTimeBetween(userId, "FINISHED", "请假",start, end);
    }

    @Override
    public Integer findWeeklyEffectiveApplicationsCount(Long  userId) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        Map<Integer, WeekData> weeks = DateUtil.weeks(YearMonth.now());
        for(Map.Entry<Integer,WeekData> entry: weeks.entrySet()){
            WeekData value = entry.getValue();
            if((LocalDate.now().equals(value.getStart()) ||LocalDate.now().isAfter(value.getStart())) && (LocalDate.now().isBefore(value.getEnd())||LocalDate.now().equals(value.getEnd()))){
                start = LocalDateTime.of(value.getStart(),LocalTime.MIN);
                end = LocalDateTime.of(value.getEnd(),LocalTime.MAX);
            }
        }
        int count = 0;
        if(start != null && end != null){
            List<Application> running = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndCreateTimeBetweenOrderByCreateTime(userId, "RUNNING", "请假",start, end);
            if(running != null){
                count+=running.size();
            }
            List<Application> finished = applicationRepository.findByUserIdAndStatusAndApplicateTypeAndCreateTimeBetweenOrderByCreateTime(userId, "FINISHED", "请假",start, end);
            if(finished != null){
                count += finished.size();
            }

        }
        return count;
    }
}
