package easyoa.leavemanager.repository.user;

import easyoa.leavemanager.domain.user.UserPosition;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by claire on 2019-06-21 - 17:10
 **/
public interface UserPositionRepository extends JpaRepository<UserPosition,Integer> {
    UserPosition findByUserCode(String userCode);
}
