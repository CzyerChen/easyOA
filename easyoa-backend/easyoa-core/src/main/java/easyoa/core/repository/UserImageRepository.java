package easyoa.core.repository;

import easyoa.core.domain.po.user.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by claire on 2019-07-01 - 10:04
 **/
public interface UserImageRepository extends JpaRepository<UserImage,Long> {
    UserImage findByName(String name);
}
