package easyoa.rulemanager.repository;

import easyoa.rulemanager.domain.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by claire on 2019-06-21 - 17:06
 **/
public interface UserDetailRepository extends JpaRepository<UserDetail,String> {
    UserDetail findByUserId(Long userId);

}
