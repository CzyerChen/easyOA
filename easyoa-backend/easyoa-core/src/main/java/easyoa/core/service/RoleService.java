package easyoa.core.service;

import easyoa.common.domain.dto.RoleDTO;
import easyoa.common.domain.vo.RoleSearch;
import easyoa.common.domain.vo.RoleVO;
import easyoa.core.domain.po.user.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * Created by claire on 2019-06-24 - 09:04
 **/
public interface RoleService {

    Role getRoleById(Integer roleId);

    Set<Role> getRoleByIds(List<Integer> ids);

    Set<Integer> getRoleIdByIds(List<Integer> ids);

    Page<Role> getRolePageWithParam(Pageable pageable, RoleSearch roleSearch);

    Page<Role> getRolePage(Pageable pageable,Role role);

    Role findbyName(String roleName);

    void addRole(RoleVO role);

    void updateRole(RoleVO role);

    void deleteRole(Integer roleId);

    void deleteRoles(List<Integer>  roles);

    List<RoleDTO> getRoles();

    void saveRoleMenu(String menus, Integer roleId);
}
