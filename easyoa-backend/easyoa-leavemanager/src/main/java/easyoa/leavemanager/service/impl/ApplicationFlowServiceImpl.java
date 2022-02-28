package easyoa.leavemanager.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import easyoa.common.constant.ApplicationStatusEnum;
import easyoa.common.constant.FlowTypeEnum;
import easyoa.common.domain.vo.FlowTimeline;
import easyoa.common.model.message.FeedBack;
import easyoa.common.utils.DateUtil;
import easyoa.core.service.UserService;
import easyoa.leavemanager.domain.GlobalFlowDTO;
import easyoa.leavemanager.domain.biz.Application;
import easyoa.leavemanager.domain.biz.ApplicationFlow;
import easyoa.leavemanager.domain.user.UserNotice;
import easyoa.leavemanager.model.FlowProcessor;
import easyoa.leavemanager.repository.biz.ApplicationFlowRepository;
import easyoa.leavemanager.runner.api.RuleServer;
import easyoa.leavemanager.runner.system.MailServer;
import easyoa.leavemanager.service.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by claire on 2019-07-15 - 18:45
 **/
@Slf4j
@Service(value = "applicationFlowService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ApplicationFlowServiceImpl implements ApplicationFlowService {
    @Autowired
    private ApplicationFlowRepository applicationFlowRepository;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private GlobalFlowService globalFlowService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserNoticeService userNoticeService;
    @Autowired
    private RuleServer ruleServer;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private FlowProcessor flowProcessor;
    @Autowired
    private UserVacationService userVacationService;
    @Autowired
    private MailServer mailServer;

    private static final String MAIL_TEXT_FOOTER="<br/> 系统登录链接：http://fiboapp.corp.com:8081/#/login";

    @Override
    public ApplicationFlow startUpFow(Application application) {
        ApplicationFlow flow = applicationFlowRepository.findFirstByApplicationId(application.getId());
        if (flow == null) {
            List<GlobalFlowDTO> sortedFlows = getSortedFlows(application.getCompanyId());

            ApplicationFlow newFlow = new ApplicationFlow();
            newFlow.setApplicationId(application.getId());
            newFlow.setName(sortedFlows.get(1).getName());
            newFlow.setLevel(1);
            newFlow.setUserId(application.getUserId());
            newFlow.setFlowId(sortedFlows.get(1).getId());
            newFlow.setCreateTime(LocalDateTime.now());
            newFlow.setModifyTime(newFlow.getCreateTime());
            newFlow.setType(sortedFlows.size() > 1 ? FlowTypeEnum.TRANSFER.getId() : FlowTypeEnum.OVER.getId());
            //自动审核不用委派人
            newFlow.setAssigneeName(sortedFlows.get(1).getName());

            String content = initFlowMsg(newFlow);
            newFlow.setContent(content);

            return saveApplicationFlow(newFlow);
        }
        return null;
    }

    private String initFlowMsg(ApplicationFlow applicationFlow) {
        String status = applicationFlow.getStatus() == null ? "无; " : (applicationFlow.getStatus() ? " 通过; " : " 拒绝; ");
        String name = applicationFlow.getAssigneeName() == null ? "无; " : applicationFlow.getAssigneeName() + "; ";
        return "您的申请流程目前状态：" + status + "受理人：" + name;
    }

    @Override
    public ApplicationFlow saveApplicationFlow(ApplicationFlow applicationFlow) {
        return applicationFlowRepository.save(applicationFlow);
    }

    @Override
    public void terminateAllFlows(List<Long> applicationIds) {
        List<ApplicationFlow> list = applicationFlowRepository.findByApplicationIdIn(applicationIds);
        List<ApplicationFlow> flows = list.stream().filter(a -> a.getType().equals(FlowTypeEnum.TRANSFER.getId()) || a.getType().equals(FlowTypeEnum.REDO.getId()))
                .collect(Collectors.toList());
        flows.forEach(a ->{
            a.setType(FlowTypeEnum.TERMINATE.getId());
            a.setModifyTime(LocalDateTime.now());
        });
        applicationFlowRepository.saveAll(flows);
    }

    @Override
    public void noticeAssignees(List<Long> applicationIds) {
        List<UserNotice> notices = new ArrayList<>();
        if (applicationIds != null) {
            applicationIds.forEach(id -> {
                List<ApplicationFlow> list = applicationFlowRepository.findByTypeAndApplicationIdIn(FlowTypeEnum.TERMINATE.getId(), id);
                List<Long> assignees = list.stream().filter(a -> null != a.getAssignee()).map(ApplicationFlow::getAssignee).distinct().collect(Collectors.toList());
                if (assignees != null) {
                    String name = userService.getUserNameById(list.get(0).getUserId());
                    String message = initTerminateNoticeMessage(id, name);

                    assignees.forEach(a -> {
                        UserNotice notice = new UserNotice();
                        notice.setUserId(a);
                        notice.setMessage(message);
                        notice.setCreateDate(LocalDate.now());
                        notices.add(notice);
                    });
                }
            });
        }
        if (notices.size() != 0) {
            userNoticeService.saveUserNotice(notices);
        }
    }

    @Override
    public List<ApplicationFlow> getFlowsRaletedWithApplicationId(Long applicationId) {
        return applicationFlowRepository.findByApplicationId(applicationId);
    }

    @Override
    public List<FlowTimeline> buildTimelineForOverall(List<ApplicationFlow> list) {
        List<FlowTimeline> lines = new ArrayList<>();
        List<Long> appIds = list.stream().map(ApplicationFlow::getApplicationId).distinct().collect(Collectors.toList());
        if (appIds != null) {
            Map<Long, String> map = applicationService.findApplicationNameByIds(appIds);
            if (!map.isEmpty()) {
                list.forEach(f -> {
                    FlowTimeline line = new FlowTimeline();
                    String name = map.get(f.getApplicationId());
                    line.setTag(f.getCreateTime() == null ? "" : DateUtil.formatFullTime(f.getCreateTime(), DateUtil.BASE_PATTERN));
                    line.setContent("申请ID:" + f.getId() + "; 请假类型: " + name + " 申请详情:" + f.getContent());
                    lines.add(line);
                });
            }
        }


        return lines;
    }

    @Override
    public List<FlowTimeline> buildTimeline(List<ApplicationFlow> list) {
        List<FlowTimeline> lines = new ArrayList<>();
        List<ApplicationFlow> sortedList = list.stream().sorted(Comparator.comparing(ApplicationFlow::getModifyTime)).collect(Collectors.toList());
        sortedList.forEach(f -> {
            FlowTimeline line = new FlowTimeline();
            line.setTag(f.getModifyTime() == null ? "" : DateUtil.formatFullTime(f.getModifyTime(), DateUtil.BASE_PATTERN));
            line.setContent(f.getContent());
            lines.add(line);
        });
        return lines;
    }

    @Override
    //@Async("taskExecutor")
    public void basicCheckForFlow(Integer flowId, Long applicationId) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        //申请需求
        String applyResult = ruleServer.checkForApply("apply", applicationId);
        FeedBack feedBack = mapper.readValue(applyResult, new TypeReference<FeedBack>() {
        });
        //自动审核未通过申请审核，终止
        if (!feedBack.getMeet()) {
            terminateFlowAndApplication(flowId, applicationId, feedBack);
        } else {
            //进行文件审核
            String allowenceResult = ruleServer.checkForApply("allowence", applicationId);
            FeedBack feedBack1 = mapper.readValue(allowenceResult, new TypeReference<FeedBack>() {
            });
            //未通过文件审核，终止
            if (!feedBack1.getMeet()) {
                terminateFlowAndApplication(flowId, applicationId, feedBack1);
            } else {//通过自动审核，流转到下一个流程
                ApplicationFlow flow = findById(flowId);
                flow.setContinueTrans(true);
                Application application = applicationService.findById(applicationId);
                userNoticeService.saveMessage(application.getStage(), flow.getContent(), flow.getUserId(), flow.getAssigneeName(), flow.getName());
                if (flow != null) {
                    flowProcessor.handleTranfer(flow);
                }
            }
        }
    }

    private void terminateFlowAndApplication(Integer flowId, Long applicationId, FeedBack feedBack) {
        ApplicationFlow flow = findById(flowId);
        if (flow != null) {
            flow.setStatus(false);
            flow.setType(FlowTypeEnum.TERMINATE.getId());
            flow.setContent("您的申请流程目前状态：拒绝; 受理人：自动审核; " + feedBack.getInvalidMessage());
            flow.setModifyTime(LocalDateTime.now());
            saveApplicationFlow(flow);

            Application application = applicationService.findById(applicationId);
            application.setStatus(ApplicationStatusEnum.TERMINATED.toString());
            application.setStage(application.getStage() + ": 流程已被终止");
            applicationService.updateApplication(application);

            userNoticeService.saveMessage(application.getStage(), flow.getContent(), application.getUserId(), flow.getAssigneeName(), flow.getName());
            try {
                mailServer.sendSimpleMail(application.getUserName(), "系统：申请流程终止通知", flow.getContent()+ MAIL_TEXT_FOOTER);
            } catch (Exception e) {
                log.error("邮件发送异常，请检查mail server ", e);
            }
            userVacationService.removeVacationCache(application);
        }
    }

    @Override
    public ApplicationFlow findById(Integer flowId) {
        return applicationFlowRepository.findById(flowId).orElse(null);
    }

    @Override
    public List<ApplicationFlow> findByAssigneeTodo(Long assignee) {
        return applicationFlowRepository.findByAssigneeAndStatusIsNullAndFlowIdIsNotNull(assignee);
    }

    @Override
    public List<ApplicationFlow> findByAssigneeDone(Long assignee) {
        return applicationFlowRepository.findByAssigneeAndStatusIsNotNullAndFlowIdIsNotNull(assignee);
    }

    @Override
    public ApplicationFlow findByApplicationId(Long applicationId) {
        return applicationFlowRepository.findFirstByApplicationIdAndFlowIdIsNotNullOrderByIdDesc(applicationId);
    }

    @Override
    public List<ApplicationFlow> findByUserId(Long userId) {
        PageRequest pageRequest = new PageRequest(0, 20);
        Page<ApplicationFlow> page = applicationFlowRepository.findByUserIdOrderByCreateTimeDesc(userId, pageRequest);
        if(page != null && page.getContent() != null && page.getContent().size() != 0){
            return page.getContent();
        }
        return null;
    }

    private String initTerminateNoticeMessage(Long applicationId, String username) {
        return "您参与处理的申请:" + applicationId + ",  已被申请人 " + username + " 强制终止，当前流程已被终止，特此通知";
    }

    private List<GlobalFlowDTO> getSortedFlows(Integer companyId) {
        RList<GlobalFlowDTO> flowList = cacheService.getFlowList(companyId);
        List<GlobalFlowDTO> flows = new ArrayList<>();
        if (flowList == null) {
            List<GlobalFlowDTO> effectiveFlow = globalFlowService.findEffectiveFlow(companyId);
            if (effectiveFlow != null) {
                flows.addAll(effectiveFlow);
                flowList.addAll(effectiveFlow);
            }
        } else {
            flows.addAll(flowList);
        }
        return flows.stream().sorted(Comparator.comparingInt(GlobalFlowDTO::getId)).collect(Collectors.toList());
    }

}
