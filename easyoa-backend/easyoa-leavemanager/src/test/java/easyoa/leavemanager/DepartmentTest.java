package easyoa.leavemanager;

import easyoa.core.domain.po.user.Department;
import easyoa.core.domain.dto.DeptExcel;
import easyoa.core.repository.DepartmentRepository;
import easyoa.core.service.DepartmentService;
import easyoa.leavemanager.domain.user.UserDepartment;
import easyoa.leavemanager.repository.user.UserDepartmentRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by claire on 2019-06-27 - 17:12
 **/
public class DepartmentTest extends AbstractApplicationTest {

    @Autowired
    private UserDepartmentRepository userDepartmentRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private DepartmentService departmentService;

    @Test
    public void testUserDeptPK(){
        List<UserDepartment> all = userDepartmentRepository.findAll();
        UserDepartment build = UserDepartment.builder().userName("10001").deptId(1).build();
        userDepartmentRepository.save(build);
    }

    @Test
    public void testSaveDept(){
        Department department = new Department();
        department.setCenter("111");
        department.setCreateTime(new Date());
        department.setDeptName("dfgmbfg");
        department.setParentId(0);
        department.setLevel(0);

        departmentRepository.save(department);
    }

    @Test
    public void testExcel(){
        List<DeptExcel> list = new ArrayList<>();
        DeptExcel d1 = new DeptExcel();
        d1.setCenter("管理层");
        d1.setDeptName("总经办");
        d1.setDeptType("0");
        DeptExcel d2 = new DeptExcel();
        d2.setCenter("总经办");
        d2.setDeptName("行政");
        d2.setDeptType("1");
        DeptExcel d3 = new DeptExcel();
        d3.setCenter("行政");
        d3.setDeptName("行政1");
        d3.setDeptType("2");
        list.add(d1);
        list.add(d2);
        list.add(d3);

        departmentService.saveDepartmentWithExcel(list);

    }

    @Test
    public void testGetDeparmentPage(){
        Department d = new Department();
        d.setDeptName("行政人力部");
        Pageable pageable = new PageRequest(0,10);
        Page<Department> deptPage = departmentService.getDeptPage(d, pageable);
        Assert.assertNotNull(deptPage);
    }
}
