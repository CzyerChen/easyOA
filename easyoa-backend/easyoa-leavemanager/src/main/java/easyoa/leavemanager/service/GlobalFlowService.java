package easyoa.leavemanager.service;

import easyoa.leavemanager.domain.GlobalFlow;
import easyoa.leavemanager.domain.GlobalFlowDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-07-12 - 13:53
 **/
public interface GlobalFlowService {
    Map<String,Object> getGlobalFlowInfo(Integer companyId);

    Integer generateRoot(Integer parentId);

    void saveGlobalFlow(GlobalFlow globalFlow);

    void updateChildFlow(Integer parentId,boolean deleted);

    void deleteByIds(List<Integer> ids);

    List<GlobalFlowDTO> findEffectiveFlow(Integer companyId);

    GlobalFlow findByParentId(Integer parentId);

    GlobalFlow findById(Integer flowId);

    GlobalFlow findFirstByAssigneeNotLike(Integer root,Long userId,Integer parentFlowId);

    GlobalFlow findLastFlowByRoot(Integer root);

    Integer getRestFlowCount(Integer flowId,Integer root);

    void refreshFlowCache(Integer companyId);

    void cacheAllFlow();
}
