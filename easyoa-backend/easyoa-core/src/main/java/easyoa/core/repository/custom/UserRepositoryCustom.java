package easyoa.core.repository.custom;

import easyoa.common.domain.vo.UserSearch;
import easyoa.core.domain.po.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by claire on 2019-06-27 - 11:47
 **/
public interface UserRepositoryCustom {

    void saveUserRole(long userId,int roleId);

    List<Long> findUserIdsForRole(List<Integer> roleId);

    void deleteUserRole(List<Integer> roleId);

    void deleteUserRole(Long userId);

    List<User> findBySearchParam(UserSearch userSearch);

    Page<User> findPageBySearchParam(UserSearch userSearch, Pageable pageable);
}
