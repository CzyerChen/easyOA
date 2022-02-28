package easyoa.leavemanager.service;

import easyoa.leavemanager.domain.biz.Application;
import easyoa.leavemanager.domain.biz.ApplicationFlow;
import easyoa.common.domain.vo.FlowTimeline;

import java.io.IOException;
import java.util.List;

/**
 * Created by claire on 2019-07-15 - 18:44
 **/
public interface ApplicationFlowService {
    ApplicationFlow startUpFow(Application application);

    ApplicationFlow saveApplicationFlow(ApplicationFlow applicationFlow);

    void terminateAllFlows(List<Long> applicationIds);

    void noticeAssignees(List<Long> applicationIds);

    List<ApplicationFlow> getFlowsRaletedWithApplicationId(Long applicationId);

    List<FlowTimeline> buildTimeline(List<ApplicationFlow> list);

    void basicCheckForFlow(Integer flowId,Long applicationId) throws IOException;

    ApplicationFlow findById(Integer flowId);

    List<ApplicationFlow> findByAssigneeTodo(Long assignee);

    List<ApplicationFlow> findByAssigneeDone(Long assignee);

    ApplicationFlow findByApplicationId(Long applicationId);

    List<ApplicationFlow> findByUserId(Long userId);

    List<FlowTimeline> buildTimelineForOverall(List<ApplicationFlow> list);

}
