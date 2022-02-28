package easyoa.rulemanager.repository;

import easyoa.rulemanager.domain.UserVacationCal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

/**
 * Created by claire on 2019-08-06 - 16:40
 **/
public interface UserVacationCalRepository  extends JpaRepository<UserVacationCal,Integer> {
    UserVacationCal findByUserCodeAndCalculateMonth(String userCode, LocalDate month);
}
