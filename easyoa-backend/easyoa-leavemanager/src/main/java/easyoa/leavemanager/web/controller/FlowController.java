package easyoa.leavemanager.web.controller;

import easyoa.common.domain.ApiResponse;
import easyoa.common.domain.vo.FlowTimeline;
import easyoa.common.domain.vo.GlobalFlowForm;
import easyoa.common.utils.DateUtil;
import easyoa.leavemanager.domain.GlobalFlow;
import easyoa.leavemanager.domain.biz.ApplicationFlow;
import easyoa.leavemanager.model.FlowProcessor;
import easyoa.leavemanager.service.ApplicationFlowService;
import easyoa.leavemanager.service.GlobalFlowService;
import easyoa.leavemanager.web.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static easyoa.common.constant.FlowOperConstant.*;

/**
 * Created by claire on 2019-07-12 - 13:50
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/flow")
@Api(value = "申请流程相关接口",description="申请流程相关接口")
public class FlowController extends AbstractController {
    @Autowired
    private GlobalFlowService globalFlowService;
    @Autowired
    private ApplicationFlowService applicationFlowService;
    @Autowired
    private FlowProcessor flowProcessor;

    /**
     *  获取流程详情
     * @return
     */
    @ApiOperation(value = "获取流程详情",notes = "管理人员专用")
    @GetMapping
    @RequiresPermissions("flow:view")
    public Map<String, Object> getFlowInfo() {
        return globalFlowService.getGlobalFlowInfo(getCompanyPermission());
    }

    /**
     *  新增流程
     * @param globalFlow
     */
    @ApiOperation(value = "新增流程",notes = "管理人员专用")
    @PostMapping
    @RequiresPermissions("flow:add")
    public void addFlow(@Valid GlobalFlow globalFlow) {
        Integer companyPermission = getCompanyPermission();
        if(Objects.nonNull(companyPermission)&& companyPermission>0){
            globalFlow.setCompanyId(companyPermission);
        }
        globalFlow.setCreateTime(LocalDate.now());
        globalFlow.setUpdateTime(globalFlow.getCreateTime());
        if (0 == globalFlow.getParentId()) {
            globalFlow.setRoot(-1);
        } else {
            Integer root = globalFlowService.generateRoot(globalFlow.getParentId());
            globalFlow.setRoot(root);
        }

        globalFlowService.saveGlobalFlow(globalFlow);
    }

    /**
     * 修改流程
     * @param form
     */
    @ApiOperation(value = "修改流程",notes = "管理人员专用")
    @PutMapping
    @RequiresPermissions("flow:edit")
    public void updateFlow(@Valid GlobalFlowForm form) {
        Integer companyPermission = getCompanyPermission();
        form.setCompanyId(companyPermission);
        
        GlobalFlow globalFlow = form2Flow(form);
        if (globalFlow != null) {
            if (0 == globalFlow.getParentId()) {
                globalFlow.setRoot(-1);
            } else {
                Integer root = globalFlowService.generateRoot(globalFlow.getParentId());
                globalFlow.setRoot(root);
            }
            globalFlow.setUpdateTime(LocalDate.now());
            globalFlowService.saveGlobalFlow(globalFlow);
            globalFlowService.updateChildFlow(globalFlow.getId(), false);
        }
    }

    /**
     *  删除流程
     * @param ids
     */
    @ApiOperation(value = "删除流程",notes = "管理人员专用")
    @DeleteMapping("{ids}")
    @RequiresPermissions("flow:delete")
    public void deleteFlow(@NotBlank @PathVariable String ids) {
        List<String> flowIds = Arrays.asList(ids.split(","));
        globalFlowService.deleteByIds(flowIds.stream().map(Integer::valueOf).distinct().collect(Collectors.toList()));
        globalFlowService.refreshFlowCache(getCompanyPermission());
    }

    /**
     *  获取申请相关流程
     * @param applicationId
     * @return
     */
    @ApiOperation(value = "获取申请相关流程")
    @GetMapping("{applicationId}")
    @RequiresPermissions("application:view")
    public ApiResponse getRelatedFlows(@NotBlank @PathVariable String applicationId) {
        List<ApplicationFlow> list = applicationFlowService.getFlowsRaletedWithApplicationId(Long.valueOf(applicationId));
        if(list!= null){
            List<FlowTimeline> timelines = applicationFlowService.buildTimeline(list);
            return ApiResponse.success(timelines);
        }
        return ApiResponse.success();
    }

    /**
     *  申请单的审批操作
     * @param type
     * @param applicationIds
     */
    @ApiOperation(value = "申请单的审批操作",notes = "审批人员专用")
    @DeleteMapping("/operate/{type}/{applicationIds}")
    @RequiresPermissions("application:view")
    public void operateForApplicationFlow(@NotBlank @PathVariable(name = "type")String type,@NotBlank @PathVariable(name = "applicationIds") String applicationIds){
        ArrayList<String> list = new ArrayList<>(Arrays.asList(applicationIds.split(",")));
        if(list != null && list.size() != 0) {
            list.forEach( applicationId ->{
                ApplicationFlow flow = applicationFlowService.findByApplicationId(Long.valueOf(applicationId));
                if (flow != null) {
                    switch (type) {
                        case TRANSFER://流转
                            flowProcessor.handleTranfer(flow);
                            break;
                        case OVER://结束
                            //结束，指流程正常结束，在人事主管处
                            flowProcessor.handleOver(flow);
                            break;
                        case TERMINATE://终止
                            //终止，出现在拒绝申请，自动申请的情况
                            flowProcessor.handleTerminate(flow);
                            break;
                        case REDO://重做
                            //重做，出现在重新分配受理人情况
                            flowProcessor.handleRedo(flow);
                            break;
                        case CANCEL://撤销
                            //撤销，出现在填写异常取消表单、完成流程但计划右边需要回滚的情况
                            flowProcessor.handleCancel(flow);
                            break;
                        case FINISH://强制通过，通过当前流程流转
                            flowProcessor.handleTranfer(flow);
                            break;
                        default:
                    }
                }
            });
        }
    }

    /**
     *  获取用户所有流程
     * @param userId
     * @return
     */
    @ApiOperation(value = "获取用户所有流程")
    @GetMapping("/list/{userId}")
    @RequiresPermissions("application:view")
    public ApiResponse getUserApplicationFlows(@PathVariable Long userId){
        List<ApplicationFlow> list = applicationFlowService.findByUserId(userId);
        if(list!= null && list.size() != 0){
            List<FlowTimeline> lines = applicationFlowService.buildTimelineForOverall(list);
            if(lines != null){
                return ApiResponse.success(lines);
            }else{
                ApiResponse.success(Collections.emptyList());
            }
        }
        return ApiResponse.success();
    }


    /**
     *  实体转换
     * @param form
     * @return
     */
    private GlobalFlow form2Flow(GlobalFlowForm form) {
        GlobalFlow flow = new GlobalFlow();
        if (form != null) {
            if (StringUtils.isNotBlank(form.getId())) {
                flow.setId(Integer.valueOf(form.getId()));
            }
            if (StringUtils.isNotBlank(form.getName())) {
                flow.setName(form.getName());
            }
            if (StringUtils.isNotBlank(form.getContent())) {
                flow.setContent(form.getContent());
            }
            if (StringUtils.isNotBlank(form.getAssigneeIds())) {
                flow.setAssigneeIds(form.getAssigneeIds());
            }
            if (StringUtils.isNotBlank(form.getCreateTime())) {
                flow.setCreateTime(DateUtil.parseLocalDateWithAlias(form.getCreateTime()));
            }
            if (null != form.getDeleted()) {
                flow.setDeleted(form.getDeleted());
            }
            if (null != form.getParentId()) {
                flow.setParentId(form.getParentId());
            }
            if(Objects.nonNull(form.getCompanyId())){
                flow.setCompanyId(form.getCompanyId());
            }
        }
        return flow;
    }
}
