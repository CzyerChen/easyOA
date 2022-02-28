package easyoa.leavemanager;

import easyoa.leavemanager.domain.GlobalFlow;
import easyoa.leavemanager.domain.biz.ApplicationFlow;
import easyoa.leavemanager.service.ApplicationFlowService;
import easyoa.leavemanager.service.GlobalFlowService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-07-12 - 14:47
 **/
public class FlowTest extends AbstractApplicationTest {
    @Autowired
    private GlobalFlowService globalFlowService;
    @Autowired
    private ApplicationFlowService applicationFlowService;

    @Test
    public void testGetFlowTree(){
        Map<String, Object> globalFlowInfo = globalFlowService.getGlobalFlowInfo(null);
        Assert.assertNotNull(globalFlowInfo);
    }

    @Test
    public void testUpdateChildFlow(){
        globalFlowService.updateChildFlow(10,true);
    }


    @Test
    public void testFindByApplicationId(){
        ApplicationFlow applicationFlow = applicationFlowService.findByApplicationId(37L);
        Assert.assertNotNull(applicationFlow);
    }

    @Test
    public void testFindByApplicationId1(){
        List<ApplicationFlow> assigneeDone = applicationFlowService.findByAssigneeDone(8L);
        Assert.assertNotNull(assigneeDone);
    }

    @Test
    public void testFlow(){
        GlobalFlow lastFlowByRoot = globalFlowService.findLastFlowByRoot(1);
        Assert.assertNotNull(lastFlowByRoot);
    }

    @Test
    public void testSkipNullFlow(){
        ApplicationFlow flow = applicationFlowService.findByApplicationId(66L);
        Assert.assertNotNull(flow);
    }

    @Test
    public void testFindByAssigee(){
        List<ApplicationFlow> assigneeTodo = applicationFlowService.findByAssigneeTodo(39L);
        Assert.assertNotNull(assigneeTodo);
    }
}
