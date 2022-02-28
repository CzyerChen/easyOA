package easyoa.core.repository;

import easyoa.core.domain.po.user.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by claire on 2019-06-21 - 17:06
 **/
public interface UserDetailRepository extends JpaRepository<UserDetail,String> {
    UserDetail findByUserId(Long userId);

    List<UserDetail> findByUserIdIn(List<Long> userIds);

    UserDetail findFirstByEmail(String email);

    List<UserDetail> findByChineseNameInAndDeleted(List<String> names,Boolean deleted);
}
