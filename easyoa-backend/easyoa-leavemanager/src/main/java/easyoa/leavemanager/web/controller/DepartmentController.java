package easyoa.leavemanager.web.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.wuwenze.poi.ExcelKit;
import com.wuwenze.poi.handler.ExcelReadHandler;
import com.wuwenze.poi.pojo.ExcelErrorField;
import easyoa.leavemanager.web.AbstractController;
import easyoa.common.domain.ApiResponse;
import easyoa.common.domain.vo.DeptSearch;
import easyoa.common.exception.BussinessException;
import easyoa.core.domain.po.user.Department;
import easyoa.core.domain.dto.DeptExcel;
import easyoa.core.model.TreeNode;
import easyoa.core.service.CompanyService;
import easyoa.core.service.DepartmentService;
import easyoa.leavemanager.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-06-26 - 22:03
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/dept")
@Api(value="部门相关接口",description = "部门相关接口")
public class DepartmentController extends AbstractController {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private CompanyService companyService;

    /**
     * 获取部门列表
     * @param deptSearch
     * @return
     */
    @ApiOperation(value = "获取部门列表",notes = "管理人员专用")
    @GetMapping
    @RequiresPermissions("dept:view")
    public Map<String, Object> deptList(DeptSearch deptSearch) {
        try {
            Integer companyPermission = getCompanyPermission();
            if(Objects.nonNull(companyPermission)){
               deptSearch.setCompanyId(companyPermission);
            }
            List<Department> all = departmentService.findAllWithParams(deptSearch);
            companyService.fillCompanyNames(all);
            TreeNode<Department> treeNodes = departmentService.buildDepartTree(all);
            return getMap(treeNodes, all.size());
        }catch (Exception e){
            log.error("部门查询失败",e);
            return  getMap(null,0);
        }
    }

    /**
     *  上传部门组织关系
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "上传部门列表",notes = "管理人员专用")
    @PostMapping("/upload")
    @RequiresPermissions("dept:add")
    public ApiResponse uploadDeptFile(@RequestParam("excel-dept") MultipartFile file) throws IOException {
        final List<DeptExcel> data = Lists.newArrayList();
        //异常的数据，需要提示或者处理
        final List<Map<String, Object>> error = Lists.newArrayList();
        ExcelKit.$Import(DeptExcel.class).readXlsx(file.getInputStream(), new ExcelReadHandler<DeptExcel>() {
            @Override
            public void onSuccess(int i, int i1, DeptExcel deptExcel) {
                data.add(deptExcel);
            }

            @Override
            public void onError(int sheet, int row, List<ExcelErrorField> list) {
                // 数据校验失败时，记录到 error集合
                error.add(ImmutableMap.of("row", row, "errorFields", list));
            }
        });//ExcelUtils.importExcelWithEasyPOI(file.getInputStream(), 0, 1, DeptExcel.class);

        //删除空行,过滤实习生一行，判断需要所有内容非空
        List<DeptExcel> deptExcels = data.stream().filter(d ->
                (!"实习生".equals(d.getDeptName())) && StringUtils.isNotBlank(d.getDeptName()) && StringUtils.isNotBlank(d.getDeptType())
        ).collect(Collectors.toList());
        //所以，实习生怎么处理
        departmentService.saveDepartmentWithExcel(deptExcels);

        return ApiResponse.success();
    }

    /**
     * 新增部门
     * @param department
     * @throws BussinessException
     */
    @ApiOperation(value = "新增部门",notes = "管理人员专用")
    @Log("新增部门")
    @PostMapping
    @RequiresPermissions("dept:add")
    public void addDepartment(Department department)throws BussinessException {
        try {
            departmentService.saveDepartment(department);
        }catch (Exception e) {
            log.error("新增部门失败", e);
            throw new BussinessException("新增部门失败");
        }
    }

    /**
     * 修改部门
     * @param department
     * @throws BussinessException
     */
    @ApiOperation(value = "修改部门",notes = "管理人员专用")
    @Log("修改部门")
    @PutMapping
    @RequiresPermissions("dept:update")
    public void updateDept(Department department)throws BussinessException {
         try{
             departmentService.updateDepartment(department);
         }catch (Exception e){
             log.error("修改部门失败",e);
             throw new BussinessException("修改部门失败");
         }
    }

    /**
     * 递归删除部门
     * @param deptIds
     * @throws BussinessException
     */
    @ApiOperation(value = "递归删除部门",notes = "管理人员专用")
    @Log("删除部门")
    @DeleteMapping("{deptIds}")
    @RequiresPermissions("dept:delete")
    public  void  deleteDept(@NotBlank(message = "{required}")@PathVariable String deptIds)throws BussinessException {
        try{
            List<String> ids = Arrays.asList(deptIds.split(StringPool.COMMA));
            if(ids.size() != 0){
                departmentService.removeDepartments(ids.stream().map(Integer::valueOf).distinct().collect(Collectors.toList()));
            }
        }catch (Exception e){
            log.error("删除部门失败",e);
            throw new BussinessException("删除部门失败");
        }
    }
}
