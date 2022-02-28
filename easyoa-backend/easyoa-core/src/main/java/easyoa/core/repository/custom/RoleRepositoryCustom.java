package easyoa.core.repository.custom;

import easyoa.common.domain.vo.RoleSearch;
import easyoa.core.domain.po.user.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by claire on 2019-06-28 - 14:37
 **/
public interface RoleRepositoryCustom {

    List<Role> findRolePageBySearParam(RoleSearch roleSearch);

    Page<Role> findRolePageBySearParam(RoleSearch roleSearch, Pageable pageable);

    void saveRoleMenu(List<Integer> ids, Integer roleId);

    void deleteRoleMenu(Integer roleId);
}
