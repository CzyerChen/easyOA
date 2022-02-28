/**
 * Author:   claire
 * Date:    2022/2/18 - 1:02 下午
 * Description: 公司管理控制器
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2022/2/18 - 1:02 下午          V1.0.0          公司管理控制器
 */
package easyoa.leavemanager.web.controller;

import easyoa.common.domain.ApiResponse;
import easyoa.common.domain.PageRequestEntry;
import easyoa.common.domain.dto.CompanyDTO;
import easyoa.common.domain.vo.CompanySearch;
import easyoa.common.exception.BussinessException;
import easyoa.core.domain.po.user.Company;
import easyoa.core.service.CompanyService;
import easyoa.leavemanager.annotation.Log;
import easyoa.leavemanager.web.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能简述
 * 〈公司管理控制器〉
 *
 * @author claire
 * @date 2022/2/18 - 1:02 下午
 * @since 1.0.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/company")
@Api(value = "公司管理相关接口")
public class CompanyController extends AbstractController {
    @Autowired
    private CompanyService companyService;

    @ApiOperation(value = "获取公司列表", notes = "管理人员专用")
    @GetMapping("list")
    public ApiResponse listCompanies() {
        Integer companyPermission = getCompanyPermission();
        if (Objects.isNull(companyPermission)) {
            return ApiResponse.success(companyService.findAll());
        }else {
            return ApiResponse.success(companyService.findByIds(Collections.singletonList(companyPermission)));
        }
    }

    @ApiOperation(value = "获取公司分页列表", notes = "超级管理人员专用")
    @GetMapping
    @RequiresPermissions("company:view")
    public Map<String, Object> listPageCompanies(PageRequestEntry entry, CompanySearch companySearch) {
        Pageable pageable = getPageRequest(entry);
        Page<CompanyDTO> dtoPage = companyService.findAll(pageable, companySearch);
        return getMap(dtoPage);
    }

    @ApiOperation(value = "新增公司", notes = "超级管理人员专用")
    @Log("新增公司")
    @PostMapping
    @RequiresPermissions("company:add")
    public void addCompany(Company company) {
        try {
            companyService.save(company);
        } catch (Exception e) {
            log.error("新增公司失败", e);
            throw new BussinessException("新增公司失败");
        }
    }

    @ApiOperation(value = "修改公司", notes = "超级管理人员专用")
    @Log("修改公司")
    @PutMapping
    @RequiresPermissions("company:update")
    public void updateCompany(Company company) throws BussinessException {
        try {
            companyService.update(company);
        } catch (Exception e) {
            log.error("修改公司失败", e);
            throw new BussinessException("修改公司失败");
        }
    }

    @ApiOperation(value = "删除公司", notes = "超级管理人员专用")
    @Log("删除公司")
    @DeleteMapping("{cpyIds}")
    @RequiresPermissions("company:delete")
    public void delCompanies(@PathVariable String cpyIds) {
        companyService.deleteByIdIn(Arrays.stream(cpyIds.split(",")).map(Integer::valueOf).collect(Collectors.toList()));
    }
}
