package easyoa.rulemanager.service;

import easyoa.rulemanager.domain.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by claire on 2019-08-06 - 16:38
 **/
public interface UserVacationCalService {
    UserVacationCal getUserVacationCalByUserCode(String userCode, LocalDate month);

    void saveDefaultVacationCal(UserVacation userVacation);

    void updateVacationCal(UserVacationCal userVacationCal);

    void calForUser(UserDetail userDetail, LocalDate month);

    void reCalForVacation(List<Application> applies, UserDetail userDetail, UserVacation userVacation, User user, UserVacationCal userVacationCal);
}
