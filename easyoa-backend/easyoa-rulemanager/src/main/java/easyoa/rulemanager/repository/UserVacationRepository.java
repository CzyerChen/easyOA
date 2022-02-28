package easyoa.rulemanager.repository;

import easyoa.rulemanager.domain.UserVacation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by claire on 2019-07-17 - 09:57
 **/
public interface UserVacationRepository extends JpaRepository<UserVacation,Long> {
    UserVacation findByUserIdAndYear(Long userId,Integer year);
}
