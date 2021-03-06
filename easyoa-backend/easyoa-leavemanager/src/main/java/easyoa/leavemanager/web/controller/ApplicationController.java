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
@Api(value = "?????????????????????", description = "?????????????????????")
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
     * ????????????????????????????????????
     *
     * @param applyVo
     * @return
     */
    @ApiOperation(value = "??????????????????????????????")
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
            log.error("??????jackson string ???????????????????????????????????????{}", e);
            throw new BussinessException("??????????????????");
        }
        return ApiResponse.success();
    }

    /**
     * ???????????????
     *
     * @param applicationForm
     * @return
     */
    @ApiOperation(value = "???????????????", notes = "???????????????????????????")
    @PostMapping
    @RequiresPermissions("application:add")
    public ApiResponse saveApplication(@Valid ApplicationForm applicationForm) {
        //?????????????????????leaveFile,???????????????ID
        Long fileId = null;
        try {
            if (StringUtils.isNotBlank(applicationForm.getUploadFile())) {
                fileId = fileServer.uploadFile(applicationForm.getUploadFile(), applicationForm.getUserId());
            }
        } catch (Exception e) {
            log.error("????????????????????????{}", e);
            throw new BussinessException("????????????????????????,?????????????????????");
        }
        //?????????????????????
        ApplicationFeedBack applicationFeedBack = new ApplicationFeedBack();
        try {
            Application application = applicationService.saveApply(applicationForm, fileId);
            if (application != null) {
                generateFeedBack(application, null, applicationFeedBack);
                //?????????????????????
                ApplicationFlow applicationFlow = applicationFlowService.startUpFow(application);
                //???????????????
                if (applicationFlow != null) {
                    //??????????????????
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

                    //???????????????
                    generateFeedBack(null, applicationFlow, applicationFeedBack);
                    //???????????????????????????????????????
                    applicationFlowService.basicCheckForFlow(applicationFlow.getId(), application.getId());
                } else {
                    log.error("????????????????????????");
                }
            } else {
                log.error("??????????????????!");
            }
        } catch (Exception e) {
            log.error("?????????????????????????????????{}", e);
            throw new BussinessException("?????????????????????????????????");
        }
        return ApiResponse.success(applicationFeedBack);
    }

    /**
     * ????????????/????????????
     *
     * @param ids
     */
    @ApiOperation(value = "???????????????", notes = "????????????????????????????????????")
    @DeleteMapping("{ids}")
    @RequiresPermissions("application:delete")
    public void terminateApplications(@NotBlank @PathVariable String ids) {
        List<String> list = new ArrayList<>(Arrays.asList(ids.split(",")));
        List<Long> idList = list.stream().map(Long::valueOf).collect(Collectors.toList());
        //?????????????????????????????????
        applicationFlowService.terminateAllFlows(idList);
        //?????????????????????????????????
        idList.forEach(l -> {
            Application application = applicationService.findById(l);
            //??????????????????,????????????????????????????????????????????????????????????
            if (FINISHED.toString().equals(application.getStatus())) {
                userVacationService.rollBackUserVacation(application);
            } else if (RUNNING.toString().equals(application.getStatus())) {
                userVacationService.removeVacationCache(application);
            }
        });
        //?????????????????????
        applicationService.terminateAllApplications(idList);
        //??????????????????????????????
        applicationFlowService.noticeAssignees(idList);
    }


    /**
     * ??????
     *
     * @param ids
     */
    @ApiOperation(value = "??????", notes = "??????????????????")
    @DeleteMapping("cancel/{ids}")
    @RequiresPermissions("application:cancel")
    public void cancelApplications(@NotBlank @PathVariable String ids) {
        List<String> list = new ArrayList<>(Arrays.asList(ids.split(",")));
        if (list != null) {
            List<Long> idList = list.stream().map(Long::valueOf).collect(Collectors.toList());
            List<Long> finishList = new ArrayList<>();
            idList.forEach(l -> {
                try {
                    //?????????????????????
                    Application application = applicationService.findById(l);
                    //??????????????????,????????????????????????????????????????????????????????????
                    if (FINISHED.toString().equals(application.getStatus())) {
                        userVacationService.rollBackUserVacation(application);
                        application.setStatus(FINISH2CANCEL.toString());
                    } else if (RUNNING.toString().equals(application.getStatus())) {
                        userVacationService.removeVacationCache(application);
                        application.setStatus(RUNNING2CANCEL.toString());
                    }

                    application.setStage(application.getStage() + ":?????????????????????");
                    application.setUpdateTime(LocalDateTime.now());
                    applicationService.updateApplication(application);
                    finishList.add(l);
                }catch (Exception e){
                    log.error("meet error",e);
                }
            });
            if(!CollectionUtils.isEmpty(finishList)) {
                //??????????????????
                applicationFlowService.terminateAllFlows(finishList);
            }

        }
    }

    /**
     * ???????????????,??????????????????????????????????????????????????????
     *
     * @return
     */
    @ApiOperation(value = "?????????????????????", notes = "??????????????????")
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
     * ?????????  ?????????
     *
     * @param assignee
     * @return
     */
    @ApiOperation(value = "??????????????????????????????", notes = "???????????????")
    @GetMapping("/assignee/todo/{assignee}")
    @RequiresPermissions("process:view")
    public Map<String, Object> getApplicationsForAssignee(@PathVariable Long assignee) {
        List<Application> assignedApplications = applicationService.findAssignedApplicationsTodo(assignee);
        List<ApplicationVO> applicationVOS = applicationService.application2VOs(assignedApplications);
        return getMap(applicationVOS);
    }

    /**
     * ?????????????????????
     *
     * @param assignee
     * @return
     */
    @ApiOperation(value = "?????????????????????????????????", notes = "???????????????")
    @GetMapping("/assignee/done/{assignee}")
    @RequiresPermissions("done:view")
    public Map<String, Object> getAllApplicationsForAssignee(@PathVariable Long assignee) {
        List<Application> assignedApplications = applicationService.findAssignedApplicationsDone(assignee);
        List<ApplicationVO> applicationVOS = applicationService.application2VOs(assignedApplications);
        return getMap(applicationVOS);
    }

    /**
     * ?????????
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "?????????????????????", notes = "???????????????")
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

    @ApiOperation(value = "????????????????????????")
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

    @ApiOperation(value = "????????????????????????")
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
     * ???????????????????????????
     *
     * @param file
     * @param userId
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "??????????????????", notes = "???????????????")
    @PostMapping("/file")
    @RequiresPermissions("application:add")
    public ApiResponse uploadFile(@RequestParam("file") MultipartFile file, Long userId) throws IOException {
        try {
            LeaveFile leaveFile = uploadFile2LocalFile(userId, file, new LeaveFile());
            leaveFileService.saveFile(leaveFile);
            return ApiResponse.success(leaveFile.getFileCurrentName());
        } catch (Exception e) {
            log.error("?????????????????????????????????", e);
            throw new BussinessException("??????????????????????????????");
        }
    }


    /**
     * ???????????????????????????
     *
     * @param fileId
     * @param request
     * @param response
     * @throws IOException
     */
    @ApiOperation(value = "??????????????????", notes = "???????????????")
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
                    log.error("??????????????????", e);
                } finally {
                    in.close();
                    out.close();
                    bytes = null;
                }
            }

        } catch (Exception e) {
            log.error("??????????????????????????????", e);
            throw new BussinessException("????????????????????????");
        }
    }

    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    @PutMapping("/update")
    @RequiresPermissions("application:update")
    public void updateApplication(ApplicationVO applicationVO) throws UnsupportedEncodingException, MessagingException {
        ApplicationModifyDTO modifyDTO = applicationService.updateApplication(applicationVO);
        if (modifyDTO != null) {
            applicationService.rewriteApplicationCal(modifyDTO);
        }
    }

    /**
     * ?????????????????????
     *
     * @param entry
     * @param applicationSearch
     * @param response
     */
    @ApiOperation(value = "?????????????????????", notes = "??????????????????")
    @PostMapping("/excel")
    @RequiresPermissions("check:export")
    public void exportFile(PageRequestEntry entry, ApplicationSearch applicationSearch, HttpServletRequest request, HttpServletResponse response) {

        PageRequest pageRequest = new PageRequest(0, 10000);
        Page<Application> page = applicationService.findBySearchParam(applicationSearch, pageRequest);
        if (page != null && page.getContent() != null && page.getContent().size() != 0) {
            List<ApplicationExcel> vos = applicationService.application2Excel(page.getContent());
            log.info("???????????????????????????{}", vos.size());

            // ????????????????????????--???????????????
               /* response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
                response.setHeader("Content-Disposition", "attachment;filename=" + "???????????????" + ".xlsx");
                response.setContentType("application/vnd.ms-excel;charset=UTF-8");
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Expires", "0");//????????????
                ExcelKit.$Export(ApplicationExcel.class, response).downXlsx(vos, false);*/
            Workbook workbook = null;
            try {
                ExportParams params = new ExportParams("???????????????", null, "???????????????");
                workbook = ExcelExportUtil.exportExcel(params, ApplicationExcel.class, vos);
                workbook.write(response.getOutputStream());
                workbook.close();
            } catch (Exception e) {
                log.error("??????Excel??????", e);
                throw new BussinessException("??????Excel??????");
            } finally {
                workbook = null;
            }
        }
        log.info("???????????????????????????0");
    }


    @ApiOperation(value = "??????????????????????????????", notes = "??????????????????")
    @GetMapping("/calculate")
    @RequiresPermissions("lmsdata:view")
    public Map<String, Object> getApplicationCalculationInfo() {
        return getMap(Collections.emptyList());
    }

    /**
     * ?????????????????????
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
                fp.mkdirs();// ?????????????????????????????????????????????
            } catch (Exception e) {
                log.error("????????????????????????");
                throw new BussinessException("????????????????????????");
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
     * ?????????????????????????????????
     *
     * @param userId
     * @return
     */
    private String generateFileName(Long userId) {
        String name = DateUtil.formatDate(LocalDate.now(), DateUtil.DEFAULT_DATE_PATTERN);
        return "personal/" + userId + "/" + name + "/";
    }

    /**
     * ?????????????????????
     *
     * @param application
     * @param applicationFlow
     * @param applicationFeedBack
     */
    private void generateFeedBack(Application application, ApplicationFlow applicationFlow, ApplicationFeedBack applicationFeedBack) {
        if (application != null) {
            applicationFeedBack.setApplicationId(application.getId());
            applicationFeedBack.setContent(application.getLeaveType());
            String range = DateUtil.formatFullTime(application.getStartTime(), DateUtil.DATE_PATTERN_HOUR_MINUTE) + " ??? " +
                    DateUtil.formatFullTime(application.getEndTime(), DateUtil.DATE_PATTERN_HOUR_MINUTE);
            applicationFeedBack.setLeaveRange(range);
            applicationFeedBack.setDays(application.getDays() == null ? 0 : application.getDays());
        }
        if (applicationFlow != null) {
            applicationFeedBack.setStatus(applicationFlow.getStatus() == null ? "???" : (applicationFlow.getStatus() ? "??????" : "??????"));
            applicationFeedBack.setAssignee(applicationFlow.getAssigneeName());
        }
    }
}
