package easyoa.rulemanager.service;

import easyoa.rulemanager.domain.User;
import easyoa.rulemanager.domain.UserDetail;

import java.util.List;

/**
 * Created by claire on 2019-07-10 - 13:58
 **/
public interface UserService {
    UserDetail getUserDetailById(Long userId);

    UserDetail getUserDetailById(String id);

    Character getUserSex(Long userId);

    List<User> findAll();

    User getUser(Long userId);

    List<User> findByIds(List<Long> ids);
}
