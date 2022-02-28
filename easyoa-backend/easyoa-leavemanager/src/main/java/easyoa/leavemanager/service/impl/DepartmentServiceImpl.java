package easyoa.leavemanager.service.impl;

import easyoa.leavemanager.domain.user.UserDepartment;
import easyoa.leavemanager.utils.TreeUtil;
import easyoa.common.domain.vo.DeptSearch;
import easyoa.common.exception.BussinessException;
import easyoa.common.utils.DateUtil;
import easyoa.core.domain.po.UserDepartmentVO;
import easyoa.core.domain.po.user.Department;
import easyoa.core.domain.po.user.User;
import easyoa.core.domain.dto.DeptExcel;
import easyoa.core.model.TreeNode;
import easyoa.core.repository.DepartmentRepository;
import easyoa.core.service.DepartmentService;
import easyoa.core.service.UserService;
import easyoa.leavemanager.repository.user.UserDepartmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-06-26 - 22:09
 **/
@Slf4j
@Service("departmentService")
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDepartmentRepository userDepartmentRepository;

    @Override
    public UserDepartmentVO getUserDepartment(long userId) {
        User user = userService.getUser(userId);
        if (user != null && user.getDeptId() > 0) {
            Optional<Department> dept = departmentRepository.findById(user.getDeptId());
            if (dept.isPresent()) {
                Department department = dept.get();
                return UserDepartmentVO.builder().username(user.getUserName()).userId(userId).deptId(department.getId()).deptName(department.getDeptName()).build();
            }
        }
        return null;
    }

    /**
     * 不能用builder 它会填默认值0,导致异常
     *
     * @param depts
     */
    @Override
    public void saveDepartmentWithExcel(List<DeptExcel> depts) {
        Map<String, String> map = new HashMap<>();
        List<DeptExcel> rootCount = depts.stream().filter(d -> "0".equals(d.getDeptType())).collect(Collectors.toList());
        if (rootCount.size() != 1) {
            throw new BussinessException("excel格式有误，请查看部门架构");
        }

        //默认升序
        depts.sort(Comparator.comparingInt(o -> Integer.valueOf(o.getDeptType())));
        for (DeptExcel d : depts) {
            int type = Integer.valueOf(d.getDeptType());
            Department department = new Department();
            department.setDeptName(d.getDeptName());
            department.setDeptType(type);
            department.setLevel(type);
            department.setCenter(d.getCenter());
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setDeleted(false);

            if (type == 0) {
                department.setParentId(-1);
                department.setRoot(true);
                department.setPath(" ");
            } else {
                if (map.containsKey(d.getCenter())) {
                    String value = map.get(d.getCenter());
                    if (StringUtils.isNotBlank(value)) {
                        String[] split = value.split(",");
                        department.setParentId(Integer.valueOf(split[0]));
                        department.setPath(split[1] + "/" + split[0]);
                    }
                } else {
                    log.error("map 不存在父部门信息{},对应的子部门{}", d.getCenter(), d.getDeptName());
                }
            }
            Department saved = saveDepartment(department);
            map.put(d.getDeptName(), saved.getId() + "," + saved.getPath());
            saveDepartment(department);
        }
    }

    @Override
    public void saveDepartment(List<Department> depts) {
        departmentRepository.saveAll(depts);

    }

    @Override
    public Department saveDepartment(Department dept) {
        if (null == dept.getParentId()) {
            dept.setParentId(0);
            dept.setRoot(true);
        }else {
            dept.setRoot(false);
            Department parent = findByDeptId(dept.getParentId());
            if(parent != null ) {
                if (StringUtils.isNotBlank(parent.getDeptName())) {
                    dept.setCenter(parent.getDeptName());
                }
                dept.setPath(parent.getPath() +"/"+parent.getId());
            }
        }
        dept.setCreateTime(new Date());
        dept.setUpdateTime(dept.getCreateTime());
        dept.setDeleted(false);
        if(null !=  dept.getLevel()){
            dept.setDeptType(dept.getLevel());
        }
        return departmentRepository.save(dept);
    }


    @Override
    public void saveUserDepartmentInfo(Map<String, Integer> userDeptInfo) {
        List<UserDepartment> list = new ArrayList<>();
        userDeptInfo.forEach((k, v) -> {
            list.add(UserDepartment.builder().userName(k).deptId(v).build());
        });
        userDepartmentRepository.saveAll(list);
    }

    @Override
    public int findDeptByDeptNameAndCenter(String deptName, String center) {
        Department dept = departmentRepository.findByDeptNameAndCenterAndDeleted(deptName, center, false);
        if (dept != null) {
            return dept.getId();
        }
        return -1;
    }

    @Override
    public Department findDepartmentByName(String deptName) {
        return departmentRepository.findByDeptNameAndDeleted(deptName, false);
    }

    @Override
    public Integer findDeptIdByUsername(String username) {
        UserDepartment userDepartment = userDepartmentRepository.findByUserName(username);
        if (userDepartment != null) {
            return userDepartment.getDeptId();
        }
        return null;
    }

    @Override
    public Department findByDeptId(int deptId) {
        Optional<Department> department = departmentRepository.findById(deptId);
        return department.orElse(null);
    }

    @Override
    public Page<Department> getDeptPage(Department department, Pageable pageable) {
        Pageable pageable1 = new PageRequest((int) pageable.getOffset() / pageable.getPageSize(), pageable.getPageSize(), Sort.Direction.ASC, "level");

        if (StringUtils.isNotBlank(department.getDeptName())) {
            return departmentRepository.findByDeptNameAndDeleted(department.getDeptName(), false, pageable1);
        } else {

        }
        return null;
    }

    @Override
    public TreeNode<Department> buildDepartTree(List<Department> list) {
        if (list == null) {
            return null;
        }

        List<TreeNode<Department>> trees = new ArrayList<>();
        convertTreeNode(trees, list);
        return TreeUtil.buildTree(trees);
    }

    @Override
    public List<Department> findAll() {
        Pageable pageable = new PageRequest(0, 200);
        Page<Department> all = departmentRepository.findByDeleted(false,pageable);
        if (all != null && all.getContent() != null && all.getContent().size() != 0) {
            return all.getContent();
        }
        return null;
    }

    @Override
    public void saveUserDepartment(String username, Integer deptId) {
        UserDepartment userDept = userDepartmentRepository.findByUserName(username);
        userDept.setDeptId(deptId);
        userDepartmentRepository.save(userDept);
    }

    /**
     * deptName level parentId
     * @param department
     */
    @Override
    public void updateDepartment(Department department) {
        if(null != department.getId()){
            Optional<Department> deptOptional = departmentRepository.findById(department.getId());
            if(deptOptional.isPresent()){
                Department dept = deptOptional.get();
                if(StringUtils.isNotBlank(department.getDeptName())){
                    dept.setDeptName(department.getDeptName());
                }
                if(Objects.nonNull(department.getCompanyId())){
                    dept.setCompanyId(department.getCompanyId());
                }
                if(null != department.getLevel() && !department.getLevel().equals(dept.getLevel())){
                    dept.setLevel(department.getLevel());
                }
                if(null!=department.getParentId() && !department.getParentId().equals(dept.getParentId())){
                    dept.setParentId(department.getParentId());
                    Department parent = findByDeptId(department.getParentId());
                    if(parent != null ) {
                        if (StringUtils.isNotBlank(parent.getDeptName())) {
                            dept.setCenter(parent.getDeptName());
                        }
                        dept.setPath(parent.getPath() +"/"+parent.getId());
                    }
                }
                dept.setUpdateTime(new Date());
                departmentRepository.save(dept);
            }else {
                log.error("对应部门不存在:{}",department.getId());
            }
        }else {
            log.error("前端表单缺乏部门ID");
        }

    }

    @Override
    public void removeDepartments(List<Integer> deptIds) {
        List<Department> depts = findAllDeptsWithIn(deptIds);
        if(depts!= null && depts.size() != 0){
             depts.forEach(d -> d.setDeleted(true));
             departmentRepository.saveAll(depts);
        }
    }

    @Override
    public List<Department> findAllChildrenDepts(List<Integer> ids) {
        List<Department> parents = departmentRepository.findByDeletedAndParentIdIn(false, ids);
        if (parents == null || parents.size() == 0) {
            return new ArrayList<>();
        }

        List<Integer> list = parents.stream().map(Department::getId).distinct().collect(Collectors.toList());
        List<Department> deptList = findAllChildrenDepts(list);
        deptList.addAll(parents);
        return deptList;
    }

    @Override
    public List<Department> findAllDeptsWithIn(List<Integer> ids) {
        List<Department> parents = departmentRepository.findByDeletedAndIdIn(false,ids);
        List<Department> children = findAllChildrenDepts(ids);
        parents.addAll(children);
        return parents.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<Department> findAllWithParams(DeptSearch deptSearch) {
        return departmentRepository.findBySearchParam(deptSearch);
    }

    @Override
    public Integer countCenterByNames(List<String> centerNames) {
        return departmentRepository.countDistinctByCenterIn(centerNames);
    }

    @Override
    public Boolean checkNameExist(Department department) {
        Department dept = departmentRepository.findByCompanyIdAndDeptNameAndDeleted(department.getCompanyId(), department.getDeptName(), Boolean.FALSE);
        return Objects.nonNull(department);
    }

    @Override
    public TreeNode<Department> buildDepartTree(Page<Department> page) {
        if (page.getContent() == null || page.getContent().size() == 0) {
            return null;
        }

        List<TreeNode<Department>> trees = new ArrayList<>();
        convertTreeNode(trees, page.getContent());
        return TreeUtil.buildTree(trees);
    }

    private void convertTreeNode(List<TreeNode<Department>> treeNodes, List<Department> departments) {
        departments.forEach(d -> {
            TreeNode<Department> tree = new TreeNode<>();
            tree.setId(String.valueOf(d.getId()));
            tree.setKey(tree.getId());
            tree.setParentId(String.valueOf(d.getParentId()));
            tree.setText(d.getDeptName());
            tree.setGroupName(d.getCompanyName());
            tree.setCreateTime(DateUtil.format(d.getCreateTime().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime()));
            tree.setUpdateTime(DateUtil.format(d.getUpdateTime().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime()));
            tree.setOrderNum((double) d.getLevel());
            tree.setTitle(tree.getText());
            tree.setValue(tree.getId());
            treeNodes.add(tree);
        });
    }

}
