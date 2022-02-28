package easyoa.leavemanager.repository.user;

import easyoa.leavemanager.domain.user.UserVacation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by claire on 2019-07-22 - 11:23
 **/
public interface UserVacationRepository extends JpaRepository<UserVacation,Long> {
    UserVacation findByUserIdAndYear(Long userId, int year );
}
