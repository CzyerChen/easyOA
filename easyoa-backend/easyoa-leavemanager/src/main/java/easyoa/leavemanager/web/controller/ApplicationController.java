package easyoa.leavemanager.web.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import easyoa.common.constant.LeaveConstant;
import easyoa.common.domain.dto.ApplicationFeedBack;
import easyoa.common.constant.FlowTypeEnum;
import easyoa.common.domain.ApiResponse;
import easyoa.common.domain.PageRequestEntry;
import easyoa.common.domain.vo.ApplicationForm;
import easyoa.common.domain.vo.ApplicationSearch;
import easyoa.common.domain.vo.ApplyVO;
import easyoa.common.exception.BussinessException;
import easyoa.common.model.message.ApplyMessage;
import easyoa.common.model.message.FeedBack;
import easyoa.common.model.message.UserMessage;
import easyoa.common.utils.DateUtil;
import easyoa.core.domain.po.user.UserDetail;
import easyoa.core.service.UserDetailService;
import easyoa.leavemanager.config.properties.AppProperies;
import easyoa.leavemanager.domain.biz.Application;
import easyoa.leavemanager.domain.biz.ApplicationFlow;
import easyoa.leavemanager.domain.biz.LeaveFile;
import easyoa.leavemanager.domain.dto.ApplicationModifyDTO;
import easyoa.leavemanager.domain.vo.ApplicationExcel;
import easyoa.leavemanager.domain.vo.ApplicationVO;
import easyoa.leavemanager.runner.api.FileServer;
import easyoa.leavemanager.runner.api.RuleServer;
import easyoa.leavemanager.service.ApplicationFlowService;
import easyoa.leavemanager.service.ApplicationService;
import easyoa.leavemanager.service.LeaveFileService;
import easyoa.leavemanager.service.UserVacationService;
import easyoa.leavemanager.web.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static easyoa.common.constant.ApplicationStatusEnum.*;

/**
 * Created by claire on 2019-07-10 - 17:50
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/apply")
@Api(value = "申请单相关接口", description = "申请单相关接口")
public class ApplicationController extends AbstractController {

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private RuleServer ruleServer;
    @Autowired
    private FileServer fileServer;
    @Autowired
    private LeaveFileService leaveFileService;
    @Autowired
    private ApplicationFlowService applicationFlowService;
    @Autowired
    private AppProperies appProperies;
    @Autowired
    private UserVacationService userVacationService;
    @Autowired
    private UserDetailService userDetailService;

    /**
     * 检测用户是否符合申请条件
     *
     * @param applyVo
     * @return
     */
    @ApiOperation(value = "检查是否符合申请条件")
    @PostMapping("/check")
    @RequiresPermissions("application:view")
    public ApiResponse checkApplyCondition(@Valid ApplyVO applyVo) {
        UserMessage<ApplyMessage> userMessage = applicationService.initApplyMessage(applyVo);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String value = mapper.writeValueAsString(userMessage);
            String apiResponse = ruleServer.checkAppyInfo(value);
            FeedBack feedBack = mapper.readValue(apiResponse, new TypeReference<FeedBack>() {
            });
            if (feedBack != null) {
                return ApiResponse.success(feedBack);
            }
        } catch (Exception e) {
            log.error("处理jackson string 或远程请求出现异常，请检查{}", e);
            throw new BussinessException("申请检测异常");
        }
        return ApiResponse.success();
    }

    /**
     * 保存申请单
     *
     * @param applicationForm
     * @return
     */
    @ApiOperation(value = "保存申请单", notes = "包括初始流程的流转")
    @PostMapping
    @RequiresPermissions("application:add")
    public ApiResponse saveApplication(@Valid ApplicationForm applicationForm) {
        //上传文件，保存leaveFile,返回绑定的ID
        Long fileId = null;
        try {
            if (StringUtils.isNotBlank(applicationForm.getUploadFile())) {
                fileId = fileServer.uploadFile(applicationForm.getUploadFile(), applicationForm.getUserId());
            }
        } catch (Exception e) {
            log.error("文件远程上传异常{}", e);
            throw new BussinessException("文件远程上传异常,申请单保存失败");
        }
        //保存申请单内容
        ApplicationFeedBack applicationFeedBack = new ApplicationFeedBack();
        try {
            Application application = applicationService.saveApply(applicationForm, fileId);
            if (application != null) {
                generateFeedBack(application, null, applicationFeedBack);
                //开启绑定的流程
                ApplicationFlow applicationFlow = applicationFlowService.startUpFow(application);
                //初始化消息
                if (applicationFlow != null) {
                    //更新申请状态
                    String statusInfo = FlowTypeEnum.getStatusInfo(applicationFlow.getType());
                    switch (statusInfo) {
                        case "RUNNING":
                            application.setStatus(RUNNING.toString());
                            break;
                        case "OVER":
                            application.setStatus(FINISHED.toString());
                            break;
                        default:
                    }
                    application.setStage(applicationFlow.getName());
                    applicationService.updateApplication(application);

                    //初始化消息
                    generateFeedBack(null, applicationFlow, applicationFeedBack);
                    //开始申请资格审核和文件审核
                    applicationFlowService.basicCheckForFlow(applicationFlow.getId(), application.getId());
                } else {
                    log.error("申请流程未保存！");
                }
            } else {
                log.error("申请单未保存!");
            }
        } catch (Exception e) {
            log.error("申请单流程保存出现异常{}", e);
            throw new BussinessException("申请单流程保存出现异常");
        }
        return ApiResponse.success(applicationFeedBack);
    }

    /**
     * 终止申请/拒绝申请
     *
     * @param ids
     */
    @ApiOperation(value = "终止申请单", notes = "申请人或审批人终止申请单")
    @DeleteMapping("{ids}")
    @RequiresPermissions("application:delete")
    public void terminateApplications(@NotBlank @PathVariable String ids) {
        List<String> list = new ArrayList<>(Arrays.asList(ids.split(",")));
        List<Long> idList = list.stream().map(Long::valueOf).collect(Collectors.toList());
        //找到当前流程，将其终止
        applicationFlowService.terminateAllFlows(idList);
        //终止的时候需要消除假期
        idList.forEach(l -> {
            Application application = applicationService.findById(l);
            //添加用户假期,已完成的添加假期，未完成的清除预扣的缓存
            if (FINISHED.toString().equals(application.getStatus())) {
                userVacationService.rollBackUserVacation(application);
            } else if (RUNNING.toString().equals(application.getStatus())) {
                userVacationService.removeVacationCache(application);
            }
        });
        //修改申请的状态
        applicationService.terminateAllApplications(idList);
        //给所有委派人发送消息
        applicationFlowService.noticeAssignees(idList);
    }


    /**
     * 销假
     *
     * @param ids
     */
    @ApiOperation(value = "销假", notes = "用于撤销申请")
    @DeleteMapping("cancel/{ids}")
    @RequiresPermissions("application:cancel")
    public void cancelApplications(@NotBlank @PathVariable String ids) {
        List<String> list = new ArrayList<>(Arrays.asList(ids.split(",")));
        if (list != null) {
            List<Long> idList = list.stream().map(Long::valueOf).collect(Collectors.toList());
            List<Long> finishList = new ArrayList<>();
            idList.forEach(l -> {
                try {
                    //修改申请的状态
                    Application application = applicationService.findById(l);
                    //添加用户假期,已完成的添加假期，未完成的清除预扣的缓存
                    if (FINISHED.toString().equals(application.getStatus())) {
                        userVacationService.rollBackUserVacation(application);
                        application.setStatus(FINISH2CANCEL.toString());
                    } else if (RUNNING.toString().equals(application.getStatus())) {
                        userVacationService.removeVacationCache(application);
                        application.setStatus(RUNNING2CANCEL.toString());
                    }

                    application.setStage(application.getStage() + ":申请单已被销假");
                    application.setUpdateTime(LocalDateTime.now());
                    applicationService.updateApplication(application);
                    finishList.add(l);
                }catch (Exception e){
                    log.error("meet error",e);
                }
            });
            if(!CollectionUtils.isEmpty(finishList)) {
                //终止各项流程
                applicationFlowService.terminateAllFlows(finishList);
            }

        }
    }

    /**
     * 管理员能看,考勤人员需要获取某个月的人员申请情况
     *
     * @return
     */
    @ApiOperation(value = "获取全部申请单", notes = "考勤人员专用")
    @GetMapping
    @RequiresPermissions("check:view")
    public Map<String, Object> getAllApplications(PageRequestEntry entry, ApplicationSearch applicationSearch) {
        PageRequest pageRequest = getPageRequest(entry);
        Integer companyPermission = getCompanyPermission();
        if(Objects.nonNull(companyPermission)){
            applicationSearch.setCompanyId(companyPermission);
        }
        Page<Application> page = applicationService.findBySearchParam(applicationSearch, pageRequest);
        if (page != null && page.getContent() != null && page.getContent().size() != 0) {
            List<ApplicationExcel> vos = applicationService.application2Excel(page.getContent());
            return getMap(new PageImpl<>(vos, page.getPageable(), page.getTotalElements()));
        }
        return getEmptyMap();
    }

    /**
     * 审批者  待审批
     *
     * @param assignee
     * @return
     */
    @ApiOperation(value = "获取待审批申请单列表", notes = "审批人专用")
    @GetMapping("/assignee/todo/{assignee}")
    @RequiresPermissions("process:view")
    public Map<String, Object> getApplicationsForAssignee(@PathVariable Long assignee) {
        List<Application> assignedApplications = applicationService.findAssignedApplicationsTodo(assignee);
        List<ApplicationVO> applicationVOS = applicationService.application2VOs(assignedApplications);
        return getMap(applicationVOS);
    }

    /**
     * 已经完成的审批
     *
     * @param assignee
     * @return
     */
    @ApiOperation(value = "获取已完成的申请单列表", notes = "审批人专用")
    @GetMapping("/assignee/done/{assignee}")
    @RequiresPermissions("done:view")
    public Map<String, Object> getAllApplicationsForAssignee(@PathVariable Long assignee) {
        List<Application> assignedApplications = applicationService.findAssignedApplicationsDone(assignee);
        List<ApplicationVO> applicationVOS = applicationService.application2VOs(assignedApplications);
        return getMap(applicationVOS);
    }

    /**
     * 申请人
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "获取用户申请单", notes = "申请人专用")
    @GetMapping("/user/{userId}")
    @RequiresPermissions("application:view")
    public Map<String, Object> getApplicationForUser(@NotBlank @PathVariable String userId, PageRequestEntry entry) {
        if (entry != null) {
            PageRequest pageRequest = getPageRequest(entry);
            Page<Application> pageUserApplications = applicationService.findPageUserApplications(Long.valueOf(userId), pageRequest);
            if (pageUserApplications != null && pageUserApplications.getContent() != null && pageUserApplications.getContent().size() != 0) {
                List<ApplicationVO> vos = applicationService.application2VOs(pageUserApplications.getContent());
                return getMap(new PageImpl<>(vos, pageUserApplications.getPageable(), pageUserApplications.getTotalElements()));
            }
        } else {
            List<Application> allUserApplication = applicationService.findAllUserApplication(Long.valueOf(userId));
            if (allUserApplication != null) {
                List<ApplicationVO> vos = applicationService.application2VOs(allUserApplication);
                return getMap(vos);
            }
        }
        return getEmptyMap();
    }

    @ApiOperation(value = "获取申请类型列表")
    @GetMapping("/types/{userId}")
    public List<String> getApplyTypes(@PathVariable Long userId) {
        ArrayList<String> types = new ArrayList<>();

        UserDetail user = userDetailService.findbyUserId(userId);
        types.add(LeaveConstant.ANNUAL_LEAVE);
        types.add(LeaveConstant.CASUAL_LEAVE);
        types.add(LeaveConstant.SICK_LEAVE_NORMAL);
        types.add(LeaveConstant.FUNERAL_LEAVE);
        if (Objects.nonNull(user)) {
            if(DateUtil.dateConvertToLocalDate(user.getHireDate()).plusMonths(6).isBefore(LocalDate.now()) || userId == 69){
                types.add(LeaveConstant.SICK_LEAVE);
            }
            if (!user.getSex()) {
                if (user.getMarriage()) {
                    types.add(LeaveConstant.MATERNITY_LEAVE_1);
                    types.add(LeaveConstant.MATERNITY_LEAVE_2);

                    types.add(LeaveConstant.MATERNITY_LEAVE_3);
                    types.add(LeaveConstant.MATERNITY_LEAVE_4);
                    types.add(LeaveConstant.MATERNITY_LEAVE_5);
                } else {
                    types.add(LeaveConstant.MARRIAGE_LEAVE);
                }
            } else {
                if(user.getMarriage()) {
                    types.add(LeaveConstant.PATERNITY_LEAVE);
                }else{
                    types.add(LeaveConstant.MARRIAGE_LEAVE);
                }
            }
        } else {
            types.add(LeaveConstant.MATERNITY_LEAVE_1);
            types.add(LeaveConstant.MATERNITY_LEAVE_2);
            types.add(LeaveConstant.MATERNITY_LEAVE_3);
            types.add(LeaveConstant.MATERNITY_LEAVE_4);
            types.add(LeaveConstant.MARRIAGE_LEAVE);
            types.add(LeaveConstant.MATERNITY_LEAVE_5);
            types.add(LeaveConstant.PATERNITY_LEAVE);
        }
        return types;
    }

    @ApiOperation(value = "获取申请类型列表")
    @GetMapping("/commontypes")
    public List<String> getApplyCommonTypes() {
        ArrayList<String> types = new ArrayList<>();

        types.add(LeaveConstant.ANNUAL_LEAVE);
        types.add(LeaveConstant.CASUAL_LEAVE);
        types.add(LeaveConstant.SICK_LEAVE);
        types.add(LeaveConstant.SICK_LEAVE_NORMAL);
        types.add(LeaveConstant.FUNERAL_LEAVE);
        types.add(LeaveConstant.MATERNITY_LEAVE_1);
        types.add(LeaveConstant.MATERNITY_LEAVE_2);
        types.add(LeaveConstant.MATERNITY_LEAVE_3);
        types.add(LeaveConstant.MATERNITY_LEAVE_4);
        types.add(LeaveConstant.MARRIAGE_LEAVE);
        types.add(LeaveConstant.MATERNITY_LEAVE_5);
        types.add(LeaveConstant.PATERNITY_LEAVE);

        return types;
    }

    /**
     * 申请人上传申请附件
     *
     * @param file
     * @param userId
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "上传申请附件", notes = "申请人专用")
    @PostMapping("/file")
    @RequiresPermissions("application:add")
    public ApiResponse uploadFile(@RequestParam("file") MultipartFile file, Long userId) throws IOException {
        try {
            LeaveFile leaveFile = uploadFile2LocalFile(userId, file, new LeaveFile());
            leaveFileService.saveFile(leaveFile);
            return ApiResponse.success(leaveFile.getFileCurrentName());
        } catch (Exception e) {
            log.error("写入到本地文件发生异常", e);
            throw new BussinessException("文件写入本地发生异常");
        }
    }


    /**
     * 审批人下载申请附件
     *
     * @param fileId
     * @param request
     * @param response
     * @throws IOException
     */
    @ApiOperation(value = "下载申请附件", notes = "审批人专用")
    @PostMapping("/file/download")
    @RequiresPermissions("process:view")
    public void downloadFile(@NotBlank @RequestParam(name = "fileId") String fileId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            LeaveFile leaveFile = leaveFileService.findByFileId(Long.valueOf(fileId));
            String downloadFile = fileServer.downloadFile(String.valueOf(leaveFile.getFileId()));
            if (StringUtils.isNotBlank(downloadFile)) {
                File file = new File(downloadFile);
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
                response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(leaveFile.getFileCurrentName(), "UTF-8"));
                response.setHeader("Connection", "close");
                response.setHeader("Content-Type", "application/octet-stream");
                int len = 0;
                ServletOutputStream out = response.getOutputStream();
                byte[] bytes = new byte[1024];
                try {
                    while ((len = in.read(bytes)) != -1) {
                        out.write(bytes, 0, len);
                    }
                } catch (Exception e) {
                    log.error("文件下载失败", e);
                } finally {
                    in.close();
                    out.close();
                    bytes = null;
                }
            }

        } catch (Exception e) {
            log.error("下载远程文件发生异常", e);
            throw new BussinessException("文件下载发生异常");
        }
    }

    @ApiOperation(value = "更新用户申请", notes = "考勤人员专用")
    @PutMapping("/update")
    @RequiresPermissions("application:update")
    public void updateApplication(ApplicationVO applicationVO) throws UnsupportedEncodingException, MessagingException {
        ApplicationModifyDTO modifyDTO = applicationService.updateApplication(applicationVO);
        if (modifyDTO != null) {
            applicationService.rewriteApplicationCal(modifyDTO);
        }
    }

    /**
     * 导出申请单详情
     *
     * @param entry
     * @param applicationSearch
     * @param response
     */
    @ApiOperation(value = "导出申请单详情", notes = "考勤人员专用")
    @PostMapping("/excel")
    @RequiresPermissions("check:export")
    public void exportFile(PageRequestEntry entry, ApplicationSearch applicationSearch, HttpServletRequest request, HttpServletResponse response) {

        PageRequest pageRequest = new PageRequest(0, 10000);
        Page<Application> page = applicationService.findBySearchParam(applicationSearch, pageRequest);
        if (page != null && page.getContent() != null && page.getContent().size() != 0) {
            List<ApplicationExcel> vos = applicationService.application2Excel(page.getContent());
            log.info("导出申请单的数量：{}", vos.size());

            // 指定下载的文件名--设置响应头
               /* response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
                response.setHeader("Content-Disposition", "attachment;filename=" + "申请单详情" + ".xlsx");
                response.setContentType("application/vnd.ms-excel;charset=UTF-8");
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Expires", "0");//禁止缓存
                ExcelKit.$Export(ApplicationExcel.class, response).downXlsx(vos, false);*/
            Workbook workbook = null;
            try {
                ExportParams params = new ExportParams("申请单详情", null, "申请单详情");
                workbook = ExcelExportUtil.exportExcel(params, ApplicationExcel.class, vos);
                workbook.write(response.getOutputStream());
                workbook.close();
            } catch (Exception e) {
                log.error("导出Excel失败", e);
                throw new BussinessException("导出Excel失败");
            } finally {
                workbook = null;
            }
        }
        log.info("导出申请单的数量为0");
    }


    @ApiOperation(value = "获取用户休假统计报表", notes = "考勤人员专用")
    @GetMapping("/calculate")
    @RequiresPermissions("lmsdata:view")
    public Map<String, Object> getApplicationCalculationInfo() {
        return getMap(Collections.emptyList());
    }

    /**
     * 上传文件到本地
     *
     * @param userId
     * @param uploadFile
     * @param leaveFile
     * @return
     * @throws IOException
     */
    private LeaveFile uploadFile2LocalFile(Long userId, MultipartFile uploadFile, LeaveFile leaveFile) throws IOException {
        String filePath = appProperies.getUploadPath() + generateFileName(userId);
        leaveFile.setFileLocalPath(filePath);
        leaveFile.setFileOriginName(uploadFile.getOriginalFilename());
        File fp = new File(filePath);
        if (!fp.exists()) {
            try {
                fp.mkdirs();// 目录不存在的情况下，创建目录。
            } catch (Exception e) {
                log.error("创建目录发生异常");
                throw new BussinessException("创建目录发生异常");
            }
        }

        String newName = UUID.randomUUID().toString().replace("-", "") + "." + uploadFile.getOriginalFilename().split("\\.")[1];
        leaveFile.setFileCurrentName(newName);
        String fileName = filePath + newName;
        File file = new File(fileName);
        uploadFile.transferTo(file);

        return leaveFile;
    }

    /**
     * 初始化本地文件目录名称
     *
     * @param userId
     * @return
     */
    private String generateFileName(Long userId) {
        String name = DateUtil.formatDate(LocalDate.now(), DateUtil.DEFAULT_DATE_PATTERN);
        return "personal/" + userId + "/" + name + "/";
    }

    /**
     * 初始化返回消息
     *
     * @param application
     * @param applicationFlow
     * @param applicationFeedBack
     */
    private void generateFeedBack(Application application, ApplicationFlow applicationFlow, ApplicationFeedBack applicationFeedBack) {
        if (application != null) {
            applicationFeedBack.setApplicationId(application.getId());
            applicationFeedBack.setContent(application.getLeaveType());
            String range = DateUtil.formatFullTime(application.getStartTime(), DateUtil.DATE_PATTERN_HOUR_MINUTE) + " 至 " +
                    DateUtil.formatFullTime(application.getEndTime(), DateUtil.DATE_PATTERN_HOUR_MINUTE);
            applicationFeedBack.setLeaveRange(range);
            applicationFeedBack.setDays(application.getDays() == null ? 0 : application.getDays());
        }
        if (applicationFlow != null) {
            applicationFeedBack.setStatus(applicationFlow.getStatus() == null ? "无" : (applicationFlow.getStatus() ? "通过" : "拒绝"));
            applicationFeedBack.setAssignee(applicationFlow.getAssigneeName());
        }
    }
}
