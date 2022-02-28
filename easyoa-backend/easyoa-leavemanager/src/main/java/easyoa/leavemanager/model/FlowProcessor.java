package easyoa.leavemanager.model;

import easyoa.leavemanager.service.*;
import easyoa.common.constant.ApplicationStatusEnum;
import easyoa.common.constant.FlowTypeEnum;
import easyoa.core.domain.po.user.User;
import easyoa.core.service.UserService;
import easyoa.leavemanager.domain.GlobalFlow;
import easyoa.leavemanager.domain.biz.Application;
import easyoa.leavemanager.domain.biz.ApplicationFlow;
import easyoa.leavemanager.runner.system.MailServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-07-17 - 17:43
 **/
@Slf4j
@Component
public class FlowProcessor implements IFlowHandler<ApplicationFlow> {

    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationFlowService applicationFlowService;
    @Autowired
    private GlobalFlowService globalFlowService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private UserVacationService userVacationService;
    @Autowired
    private UserNoticeService userNoticeService;
    @Autowired
    private UserReporterService userReporterService;
    @Autowired
    private MailServer mailServer;

   private static final String MAIL_TEXT_FOOTER="<br/> 系统登录链接：http://fiboapp.corp.com:8081/#/login";


    @Override
    public void handleTranfer(ApplicationFlow flow) {
        User user = null;
        if (null != flow.getUserId()) {
            user = userService.getUser(flow.getUserId());
        }
        if (flow != null && user != null) {
            //将当前流程结束
            flow.setStatus(Boolean.TRUE);
            flow.setContent(initFlowMsg(flow) + " 流程已流转");
            flow.setModifyTime(LocalDateTime.now());
            applicationFlowService.saveApplicationFlow(flow);

            //新建流程，流转给下一流程的处理人
            Integer globalFlowId = flow.getFlowId();
            if (null != globalFlowId) {
                GlobalFlow parentFlow = globalFlowService.findById(globalFlowId);
                if(parentFlow!= null) {
                    //找到了自己属于的流程
                    GlobalFlow childFlow = null;
                    Integer flowCount = globalFlowService.getRestFlowCount(parentFlow.getId(),parentFlow.getRoot());
                    if(flow.getContinueTrans() && flowCount != null && flowCount>1) {//3天以上，需要继续流转
                        childFlow = globalFlowService.findFirstByAssigneeNotLike(parentFlow.getRoot(), flow.getUserId(), globalFlowId);
                    }else{
                        //3天以下，直接流转到最后一道流程
                        childFlow = globalFlowService.findLastFlowByRoot(parentFlow.getRoot());
                        flow.setContinueTrans(false);
                    }
                    if(childFlow != null) {
                        ApplicationFlow applicationFlow = new ApplicationFlow();
                        applicationFlow.setFlowId(childFlow.getId());
                        applicationFlow.setUserId(flow.getUserId());
                        applicationFlow.setLevel(flow.getLevel() + 1);
                        applicationFlow.setName(childFlow.getName());
                        applicationFlow.setType(FlowTypeEnum.TRANSFER.getId());
                        applicationFlow.setApplicationId(flow.getApplicationId());
                        applicationFlow.setCreateTime(LocalDateTime.now());

                        if (StringUtils.isBlank(childFlow.getAssigneeIds())) {
                            //无人流转，流程处于拒绝状态，申请pause状态，定时任务会清除
                            refuseForNoAssignee(applicationFlow);
                        } else {
                            ArrayList<String> assignees = new ArrayList<>(Arrays.asList(childFlow.getAssigneeIds().split(",")));
                            if (assignees != null) {
                                /*List<User> users = null;
                                //优先挑选本部门的人审核，为满足部门领导审核的问题
                                users = userService.getUsersByDeptAndIds(assignees.stream().map(Long::valueOf)
                                        .distinct().collect(Collectors.toList()), user.getDeptId());*/
                                //其次是任意人审核，行政人事的问题
                               /* if (users.size() == 0) {
                                    users = userService.getUserByIds(assignees.stream().map(Long::valueOf).distinct().collect(Collectors.toList()));
                                }*/
                               //找出对应用户的直接审批做过滤
                                User assignee= null;
                                if(flow.getContinueTrans()){
                                   assignee =  userReporterService.findUserAssignee(flow.getUserId(),assignees);
                                }else{
                                   assignee = userService.getUser(Long.valueOf(assignees.get(0)));
                                }
                                if (assignee !=null) {
                                    Application application = applicationService.findById(applicationFlow.getApplicationId());
                                    application.setStatus(ApplicationStatusEnum.RUNNING.toString());
                                    application.setStage(applicationFlow.getName());
                                    application.setUpdateTime(LocalDateTime.now());
                                    applicationService.updateApplication(application);
                                    if(application.getDays()>3){
                                       applicationFlow.setContinueTrans(true);
                                    }else{
                                        applicationFlow.setContinueTrans(false);
                                    }
                                    //User assignee = users.get(0);
                                    applicationFlow.setAssignee(assignee.getUserId());
                                    applicationFlow.setAssigneeName(assignee.getUserName());
                                    applicationFlow.setContent(initFlowMsg(applicationFlow));
                                    applicationFlow.setModifyTime(LocalDateTime.now());
                                    //保存流程
                                    applicationFlowService.saveApplicationFlow(applicationFlow);

                                    //提交申请的人
                                    userNoticeService.saveMessage(
                                            application.getStage(),
                                            applicationFlow.getContent(),
                                            applicationFlow.getUserId(),
                                            applicationFlow.getAssigneeName(),
                                            applicationFlow.getName());
                                    try {
                                        mailServer.sendSimpleMail(application.getUserName(), "系统：申请流程进度通知", flow.getContent() +" " +applicationFlow.getContent() +MAIL_TEXT_FOOTER);
                                        mailServer.sendSimpleMail(applicationFlow.getAssigneeName(),"系统：审批任务通知","审批人员您好，您有来自"+application.getUserName()+"的申请信息，请及时处理!"+MAIL_TEXT_FOOTER);
                                    } catch (Exception e) {
                                        log.error("邮件发送异常，请检查mail server ", e);
                                    }
                                    //审核者
                                    userNoticeService.saveMessageforAssigenee(application.getUserName(),assignee.getUserId());

                                } else {//受理人为空
                                    refuseForNoAssignee(applicationFlow);
                                }
                            } else {//受理人为空
                                refuseForNoAssignee(applicationFlow);
                            }
                        }
                    }else{//部门主管级别找不到接下来的流程
                        refuseForNoAssignee(flow);
                    }
                }else{//当前流程的数据不存在了，
                    //流程异常，出现在在线修改正在使用的流程，是后续流程的流转出现异常
                    flow.setStatus(Boolean.FALSE);
                    flow.setContent("流程异常，请联系管理人员");

                    //设置申请为pause状态
                    Application application = applicationService.findById(flow.getApplicationId());
                    application.setStatus(ApplicationStatusEnum.TERMINATED.toString());
                    application.setStage(application.getStage() + ": 流程异常，已终止");
                    application.setUpdateTime(LocalDateTime.now());
                    applicationService.updateApplication(application);

                    try {
                        mailServer.sendSimpleMail(application.getUserName(), "系统：申请流程终止通知", flow.getContent()+MAIL_TEXT_FOOTER);
                    } catch (Exception e) {
                        log.error("邮件发送异常，请检查mail server ", e);
                    }

                    userVacationService.removeVacationCache(application);
                }

            } else {
                //没有更多流程，并且当前流程并没有选择结束，而是流转，主动终止申请
                Application application = applicationService.findById(flow.getApplicationId());
                application.setStatus(ApplicationStatusEnum.FINISHED.toString());
                application.setUpdateTime(LocalDateTime.now());
                applicationService.updateApplication(application);

                try {
                    mailServer.sendSimpleMail(application.getUserName(), "系统：申请流程终止通知", "流程异常，请联系管理人员"+MAIL_TEXT_FOOTER);
                } catch (Exception e) {
                    log.error("邮件发送异常，请检查mail server ", e);
                }

                userVacationService.removeVacationCache(application);
            }

        }
    }

    @Override
    public void handleOver(ApplicationFlow flow) {
        //结束当前流程
        flow.setStatus(Boolean.TRUE);
        flow.setContent(initFlowMsg(flow) + " 流程已结束");
        flow.setModifyTime(LocalDateTime.now());
        applicationFlowService.saveApplicationFlow(flow);

        //结束当前申请
        Application application = applicationService.findById(flow.getApplicationId());
        application.setStatus(ApplicationStatusEnum.FINISHED.toString());
        application.setStage(application.getStage() + ": 流程已完成");
        application.setUpdateTime(LocalDateTime.now());
        application.setFinishTime(LocalDateTime.now());
        applicationService.updateApplication(application);

        try {
            mailServer.sendSimpleMail(application.getUserName(), "系统：申请流程成功结束通知", flow.getContent()+MAIL_TEXT_FOOTER);
        } catch (Exception e) {
            log.error("邮件发送异常，请检查mail server ", e);
        }
        //清除假期的计算
        Double previousDays = userVacationService.realizeVacationCache(application);
        application.setPreviousDays(previousDays);
        applicationService.updateApplication(application);
    }

    @Override
    public void handleTerminate(ApplicationFlow flow) {
        //当前流程处于终止
        flow.setStatus(Boolean.FALSE);
        flow.setContent(flow.getContent() + " 当前流程已被终止");
        flow.setModifyTime(LocalDateTime.now());
        applicationFlowService.saveApplicationFlow(flow);

        //当前申请也被拒绝
        Application application = applicationService.findById(flow.getApplicationId());
        application.setStatus(ApplicationStatusEnum.TERMINATED.toString());
        application.setStage(application.getStage() + ": 流程已被终止");
        application.setUpdateTime(LocalDateTime.now());
        applicationService.updateApplication(application);

        try {
            mailServer.sendSimpleMail(application.getUserName(), "系统：申请流程终止通知", flow.getContent()+MAIL_TEXT_FOOTER);
        } catch (Exception e) {
            log.error("邮件发送异常，请检查mail server ", e);
        }
        //清除假期缓存
        userVacationService.removeVacationCache(application);

    }

    @Override
    public void handleRedo(ApplicationFlow flow) {
        User user = null;
        if (null != flow.getUserId()) {
            user = userService.getUser(flow.getUserId());
        }
        if (flow != null && user != null) {
            //将当前流程结束
            flow.setStatus(Boolean.TRUE);
            flow.setContent(initFlowMsg(flow) + " 更换受理人");
            flow.setModifyTime(LocalDateTime.now());
            applicationFlowService.saveApplicationFlow(flow);

            //新建流程，流转给下一流程的处理人
            Integer globalFlowId = flow.getFlowId();
            if (null != globalFlowId) {
                GlobalFlow childFlow = globalFlowService.findById(globalFlowId);

                ApplicationFlow applicationFlow = new ApplicationFlow();
                applicationFlow.setFlowId(globalFlowId);
                applicationFlow.setUserId(flow.getUserId());
                applicationFlow.setLevel(flow.getLevel());
                applicationFlow.setName(childFlow.getName());
                applicationFlow.setType(FlowTypeEnum.REDO.getId());
                applicationFlow.setApplicationId(flow.getApplicationId());
                applicationFlow.setCreateTime(LocalDateTime.now());

                if (StringUtils.isBlank(childFlow.getAssigneeIds())) {
                    //无人流转，流程处于拒绝状态，申请pause状态，定时任务会清除
                    refuseForNoAssignee(applicationFlow);
                } else {
                    ArrayList<String> assignees = new ArrayList<>(Arrays.asList(childFlow.getAssigneeIds().split(",")));
                    if (assignees != null) {
                        List<User> users = null;
                        //优先挑选本部门的人审核，为满足部门领导审核的问题
                        users = userService.getUsersByDeptAndIds(assignees.stream().map(Long::valueOf)
                                .distinct().collect(Collectors.toList()), user.getDeptId());
                        //其次是任意人审核，行政人事的问题
                        if (users.size() == 0) {
                            users = userService.getUserByIds(assignees.stream().map(Long::valueOf).distinct().collect(Collectors.toList()));
                        }

                        if (users != null && users.size() != 0) {
                            List<User> users1 = users.stream().filter(u -> !u.getUserId().equals(flow.getAssignee())).collect(Collectors.toList());
                            if (users1 != null) {
                                //流转为同部门处理人的随机一个
                                User assignee = users1.get(0);
                                applicationFlow.setAssignee(assignee.getUserId());
                                applicationFlow.setAssigneeName(assignee.getUserName());
                                applicationFlow.setContent(initFlowMsg(applicationFlow));
                                applicationFlow.setModifyTime(LocalDateTime.now());
                                //保存流程
                                applicationFlowService.saveApplicationFlow(applicationFlow);

                                Application application = applicationService.findById(applicationFlow.getApplicationId());
                                application.setStatus(ApplicationStatusEnum.RUNNING.toString());
                                application.setUpdateTime(LocalDateTime.now());
                                application.setStage(application.getStage() + ": 更换受理人");
                                applicationService.updateApplication(application);

                                try {
                                    mailServer.sendSimpleMail(application.getUserName(), "系统：申请流程进度通知", flow.getContent() + "  "+applicationFlow.getContent()+MAIL_TEXT_FOOTER);
                                    mailServer.sendSimpleMail(applicationFlow.getAssigneeName(),"系统：审批任务通知","审批人员您好，您有来自"+application.getUserName()+"的申请信息，请及时处理!"+MAIL_TEXT_FOOTER);
                                } catch (Exception e) {
                                    log.error("邮件发送异常，请检查mail server ", e);
                                }

                            } else {//当前无人流转
                                refuseForNoAssignee(applicationFlow);
                            }
                        } else {
                            refuseForNoAssignee(applicationFlow);
                        }
                    } else {
                        //受理人为空
                        refuseForNoAssignee(applicationFlow);
                    }
                }
            } else {
                //没有更多流程，并且当前流程并没有选择结束，而是流转，主动终止申请
                Application application = applicationService.findById(flow.getApplicationId());
                application.setStatus(ApplicationStatusEnum.FINISHED.toString());
                application.setUpdateTime(LocalDateTime.now());
                applicationService.updateApplication(application);
                userVacationService.removeVacationCache(application);

                try {
                    mailServer.sendSimpleMail(application.getUserName(), "系统：申请流程终止通知", "流程异常，请联系管理人员"+MAIL_TEXT_FOOTER);
                } catch (Exception e) {
                    log.error("邮件发送异常，请检查mail server ", e);
                }
            }
        }
    }

    @Override
    public void handleFinish(ApplicationFlow flow) {
        flow.setStatus(Boolean.TRUE);
        flow.setContent("流程已结束，申请通过");
        flow.setModifyTime(LocalDateTime.now());
        applicationFlowService.saveApplicationFlow(flow);

        ApplicationFlow newflow = new ApplicationFlow();
        newflow.setApplicationId(flow.getApplicationId());
        newflow.setCreateTime(LocalDateTime.now());
        newflow.setName("流程强制通过");
        newflow.setUserId(flow.getUserId());
        newflow.setContent("您的申请流程已被强制通过");
        newflow.setModifyTime(LocalDateTime.now());
        applicationFlowService.saveApplicationFlow(newflow);

        Application application = applicationService.findById(flow.getApplicationId());
        application.setStatus(ApplicationStatusEnum.FINISHED.toString());
        application.setUpdateTime(LocalDateTime.now());
        application.setStage("人力资源审批: 流程已被强制通过");
        applicationService.updateApplication(application);

        try {
            mailServer.sendSimpleMail(application.getUserName(), "系统：申请流程进度通知", flow.getContent() + "  "+newflow.getContent()+MAIL_TEXT_FOOTER);
            mailServer.sendSimpleMail(flow.getAssigneeName(),"系统：审批通知","审批人员您好，来自"+application.getUserName()+"的申请已被人力资源部强制通过，!"+MAIL_TEXT_FOOTER);
        } catch (Exception e) {
            log.error("邮件发送异常，请检查mail server ", e);
        }

    }

    @Override
    public void handleCancel(ApplicationFlow flow) {
        //当前流程处于终止
        flow.setStatus(Boolean.FALSE);
        flow.setContent(flow.getContent() + " 当前申请已被撤销");
        flow.setModifyTime(LocalDateTime.now());
        applicationFlowService.saveApplicationFlow(flow);

        Application application = applicationService.findById(flow.getApplicationId());
        if(ApplicationStatusEnum.FINISHED.toString().equals(application.getStatus())){
            application.setStatus(ApplicationStatusEnum.FINISH2CANCEL.toString());
        }else if(ApplicationStatusEnum.RUNNING.toString().equals(application.getStatus())){
            application.setStatus(ApplicationStatusEnum.RUNNING2CANCEL.toString());
        }
        application.setStage(application.getStage() + ": 流程已被撤销");
        application.setUpdateTime(LocalDateTime.now());
        applicationService.updateApplication(application);

        try {
            mailServer.sendSimpleMail(application.getUserName(), "系统：申请流程撤销通知", flow.getContent()+MAIL_TEXT_FOOTER);
        } catch (Exception e) {
            log.error("邮件发送异常，请检查mail server ", e);
        }
        //清除假期缓存
        userVacationService.cancelVacationCache(application);
    }

    private void refuseForNoAssignee(ApplicationFlow applicationFlow) {
        //设置流程拒绝
        applicationFlow.setStatus(Boolean.FALSE);
        applicationFlow.setContent("流程缺乏受理人，当前流程已被拒绝，申请将处于暂停状态");

        //设置申请为pause状态
        Application application = applicationService.findById(applicationFlow.getApplicationId());
        application.setStatus(ApplicationStatusEnum.PAUSED.toString());
        application.setStage(application.getStage() + ": 流程缺乏受理人，已暂停");
        application.setUpdateTime(LocalDateTime.now());
        applicationService.updateApplication(application);

        try {
            mailServer.sendSimpleMail(application.getUserName(), "系统：申请流程进度暂停通知", applicationFlow.getContent()+MAIL_TEXT_FOOTER);
        } catch (Exception e) {
            log.error("邮件发送异常，请检查mail server ", e);
        }
        userVacationService.removeVacationCache(application);
    }

    private String initFlowMsg(ApplicationFlow applicationFlow) {
        String status = applicationFlow.getStatus() == null ? "无; " : (applicationFlow.getStatus() ? " 通过; " : " 拒绝; ");
        String name = applicationFlow.getAssigneeName() == null ? "无" : applicationFlow.getAssigneeName() + "; ";
        return "您的申请流程目前状态: " + status + " 受理人: " + name;
    }


    private void saveMessage(String applicationMsg,String flowMsg,Long  userId,String assignee,String flowName){
        userNoticeService.saveMessage(applicationMsg,flowMsg,userId,assignee,flowName);
    }

}
