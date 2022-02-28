package easyoa.leavemanager.web.controller;

import easyoa.common.domain.ApiResponse;
import easyoa.common.domain.PageRequestEntry;
import easyoa.common.domain.vo.LeaveRuleVO;
import easyoa.leavemanager.runner.api.RuleServer;
import easyoa.leavemanager.web.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-07-11 - 17:27
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/rule")
@Api(value = "规则相关接口",description = "规则相关接口")
public class RuleController extends AbstractController {
    @Autowired
    private RuleServer ruleServer;

    /**
     * 获取规则列表
     * @return
     */
    @ApiOperation(value = "获取规则列表",notes = "管理人员专用")
    @GetMapping
    @RequiresPermissions("rule:view")
    public ApiResponse getRuleList() {
        Integer companyPermission = getCompanyPermission();
        List<LeaveRuleVO> ruleList = ruleServer.getRuleList(companyPermission);
        if (ruleList != null) {
            return ApiResponse.success(ruleList);
        }
            return ApiResponse.success(Collections.emptyList());
    }

    /**
     * 获取规则分页结果
     * @param entry
     * @return
     */
    @ApiOperation(value = "获取规则分页结果",notes = "管理人员专用")
    @GetMapping("/page")
    @RequiresPermissions("rule:view")
    public Map<String,Object> getRulePage(PageRequestEntry entry) {
        Integer companyPermission = getCompanyPermission();
        return ruleServer.getRulePage(entry,companyPermission);
    }


    /**
     * 修改规则
     * @param leaveRuleVO
     * @return
     */
    @ApiOperation(value = "修改规则",notes = "管理人员专用")
    @PutMapping
    @RequiresPermissions("rule:edit")
    public ApiResponse updateRule(@Valid LeaveRuleVO leaveRuleVO) {
        ruleServer.updateRules(leaveRuleVO);
        return ApiResponse.success();
    }

    /**
     * 新增规则
     * @param leaveRuleVO
     * @return
     */
    @ApiOperation(value = "新增规则",notes = "管理人员专用")
    @PostMapping
    @RequiresPermissions("rule:add")
    public ApiResponse saveRule(@Valid LeaveRuleVO leaveRuleVO) {
        ruleServer.saveRules(leaveRuleVO);
        return ApiResponse.success();
    }

    /**
     * 删除规则
     * @param ruleIds
     * @return
     */
    @ApiOperation(value = "删除规则",notes = "管理人员专用")
    @DeleteMapping("{ruleIds}")
    @RequiresPermissions("rule:delete")
    public ApiResponse delateRules(@NotBlank @PathVariable String ruleIds){
       ruleServer.deleteRules(ruleIds);
       return ApiResponse.success();
    }
}
