package easyoa.core.repository;

import easyoa.core.domain.po.user.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by claire on 2019-06-24 - 15:32
 **/
public interface UserConfigRepository extends JpaRepository<UserConfig,Long> {
    UserConfig findByUserId(long userId);
}
