package easyoa.core.repository;

import easyoa.core.domain.po.user.Department;
import easyoa.core.repository.custom.DepartmentRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by claire on 2019-06-21 - 17:02
 **/
public interface DepartmentRepository extends JpaRepository<Department,Integer>, JpaSpecificationExecutor<Department>, DepartmentRepositoryCustom {
    Department findByDeptNameAndCenterAndDeleted(String deptName,String center,boolean deleted);

    Department findByDeptNameAndDeleted(String deptName,boolean deleted);

    Page<Department> findByDeptNameAndDeleted(String deptName, boolean deleted,Pageable pageable);

    List<Department> findByDeletedAndIdIn(boolean deleted, List<Integer> ids);

    List<Department> findByDeletedAndParentIdIn(boolean deleted,List<Integer> ids);

    Page<Department> findByDeleted(boolean deleted,Pageable pageable);

    @Query(value = "select count(distinct center) from fb_department where center in (:centers)",nativeQuery = true)
    int countDistinctByCenterIn(@Param("centers") List<String> centers);

    Department findByCompanyIdAndDeptNameAndDeleted(Integer companyId,String deptName,Boolean deleted);
}
