package easyoa.leavemanager.repository.user;

import easyoa.leavemanager.domain.user.UserDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by claire on 2019-06-27 - 17:12
 **/
public interface UserDepartmentRepository extends JpaRepository<UserDepartment, String> {
    UserDepartment findByUserName(String userName);

}
