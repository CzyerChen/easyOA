package easyoa.rulemanager.service;

import easyoa.rulemanager.domain.LeaveRules;
import easyoa.rulemanager.domain.UserVacation;

import java.util.List;

/**
 * Created by claire on 2019-07-17 - 09:58
 **/
public interface UserVacationService {
    UserVacation findUserVacation(Long userId,Integer year);

    UserVacation saveUserVacation(UserVacation userVacation);

    UserVacation saveDefaultVacation(Long userId);

    void updateUserVacationInfo(LeaveRules oldRule,LeaveRules newRule);

    List<UserVacation> findAllUserVacation();
}
