package easyoa.leavemanager.repository.user;

import easyoa.leavemanager.domain.user.UserReporter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by claire on 2019-07-26 - 13:38
 **/
public interface UserReporterRepository extends JpaRepository<UserReporter,Integer> {

    List<UserReporter>  findByUserCode(String userCode);

    UserReporter findFirstByUserCodeAndReporterCodeIn(String userCode, List<String> ids);

    UserReporter findByUserCodeAndReporterCode(String userCode,String reporterCode);
}
