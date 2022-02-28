package easyoa.rulemanager.service;

import easyoa.rulemanager.domain.Application;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by claire on 2019-07-17 - 09:35
 **/
public interface ApplicationService {
    List<Application> findDailyEffectiveApplications(Long userId);

    Application findById(Long applicationId);

    Integer findMonthlyEffectiveApplicationsCount(Long  userId);

    Double findApplicationCountLastMonth(Long userId);

    Double findApplicationCount(Long userId);

    List<Application> findMonthlyEffectiveApplications(Long  userId);

    List<Application> findMonthlyEffectiveApplicationsWithDate(Long  userId, LocalDate date);

    Integer findWeeklyEffectiveApplicationsCount(Long  userId);
}
