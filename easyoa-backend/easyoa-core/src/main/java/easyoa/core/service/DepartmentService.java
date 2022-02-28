package easyoa.core.service;

import easyoa.common.domain.vo.DeptSearch;
import easyoa.core.domain.po.UserDepartmentVO;
import easyoa.core.domain.po.user.Department;
import easyoa.core.domain.dto.DeptExcel;
import easyoa.core.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-06-26 - 22:06
 **/
public interface DepartmentService {
    UserDepartmentVO getUserDepartment(long userId);

    void saveDepartmentWithExcel(List<DeptExcel> depts);

    void saveDepartment(List<Department> depts);

    Department saveDepartment(Department dept);

    void saveUserDepartmentInfo(Map<String,Integer> userDeptInfo);

    int findDeptByDeptNameAndCenter(String deptName,String center);

    Department findDepartmentByName(String deptName);

    Integer findDeptIdByUsername(String username);

    Department findByDeptId(int deptId);

    Page<Department> getDeptPage(Department department, Pageable pageable);

    TreeNode<Department>  buildDepartTree(List<Department> list);

    TreeNode<Department>  buildDepartTree(Page<Department> page);

    List<Department> findAll();

    void saveUserDepartment(String username,Integer deptId);

    void updateDepartment(Department department);

    void removeDepartments(List<Integer> deptIds);

    List<Department> findAllChildrenDepts(List<Integer> ids);

    List<Department> findAllDeptsWithIn(List<Integer> ids);

    List<Department> findAllWithParams(DeptSearch deptSearch);

    Integer countCenterByNames(List<String> centerNames);

    Boolean checkNameExist(Department department);
}

