package easyoa.core.repository;

import easyoa.core.domain.po.user.User;
import easyoa.core.repository.custom.UserRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by claire on 2019-06-21 - 17:06
 **/
public interface UserRepository  extends JpaRepository<User,Long>, UserRepositoryCustom, JpaSpecificationExecutor<User> {
    User findByUserNameAndDeleted(String username,boolean deleted);

    User findByUserCodeAndDeleted(String userCode,boolean deleted);

    User findByUserIdAndDeleted(Long userId,boolean deleted);

    List<User> findByDeletedAndUserIdIn(boolean deleted,List<Long> userIds);

    @Query(value = "select user_id from fb_user_role where role_id in(select role_id  from fb_role_menu where menu_id =:menuId)",nativeQuery = true)
    List<BigInteger> findUserIdsForMenu(int menuId);

    @Query(value = "select user_id from fb_user_role where role_id =:roleId",nativeQuery = true)
    List<BigInteger> findUserIdsForRole(int roleId);

    Page<User> findByDeleted(boolean deleted, Pageable pageable);

    List<User> findByUserIdIn(List<Long> userIds);

    List<User> findByDeletedAndDeptIdAndUserIdIn(boolean deleted,Integer deptId,List<Long> userIds);

    List<User> findByDeletedAndCompanyId(Boolean deleted,Integer companyId);

    @Query(value = "select distinct company_id from fb_user where deleted=0 ",nativeQuery = true)
    List<Integer> findDistinctByCompanyIdAndDeleted();

    @Query(value = "select user_id from fb_user where deleted=0 and company_id=:companyId ",nativeQuery = true)
    List<Integer> findUserIdByCompanyId(@Param("companyId") Integer companyId);
}
