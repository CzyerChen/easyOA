package easyoa.rulemanager.repository;

import easyoa.rulemanager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by claire on 2019-07-10 - 16:28
 **/
public interface UserRepository extends JpaRepository<User,Long> {
    User  findByUserId(Long userId);

    List<User> findByDeletedAndStatus(Boolean deleted,Integer status);

    List<User> findByDeletedAndStatusAndUserIdIn(Boolean deleted,Integer status,List<Long> ids);
}
