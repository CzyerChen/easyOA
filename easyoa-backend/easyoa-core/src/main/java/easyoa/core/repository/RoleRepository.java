package easyoa.core.repository;

import easyoa.core.domain.po.user.Role;
import easyoa.core.repository.custom.RoleRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

/**
 * Created by claire on 2019-06-21 - 17:05
 **/
public interface RoleRepository extends JpaRepository<Role,Integer> , JpaSpecificationExecutor<Role>, RoleRepositoryCustom {
    Role findByRoleIdAndDeleted(int roleId, boolean deleted);

    Set<Role> findByDeletedAndRoleIdIn(boolean deleted,List<Integer> ids);

    Set<Role> findByRoleIdIn(List<Integer> ids);

    Page<Role> findByRoleNameAndDeleted(String roleName,boolean deleted,Pageable pageable);

    Role  findByRoleNameAndDeleted(String roleName,boolean deleted);

    Page<Role> findByDeleted(boolean deleted,Pageable pageable);
}
