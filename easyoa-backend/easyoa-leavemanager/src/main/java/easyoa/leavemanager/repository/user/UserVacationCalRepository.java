package easyoa.leavemanager.repository.user;

import easyoa.leavemanager.domain.user.UserVacationCal;
import easyoa.leavemanager.repository.user.custom.UserVacationCalRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

/**
 * Created by claire on 2019-08-07 - 09:46
 **/
public interface UserVacationCalRepository  extends JpaRepository<UserVacationCal,Integer>, JpaSpecificationExecutor<UserVacationCal>, UserVacationCalRepositoryCustom {
    UserVacationCal findByUserCodeAndCalculateMonthGreaterThanEqual (String userCode,LocalDate date);

    UserVacationCal findByUserCode(String userCode);

    UserVacationCal findByUserIdAndCalculateMonthBetween(Long userId,LocalDate from,LocalDate to);


}
