package easyoa.rulemanager.service;

import easyoa.common.domain.vo.LeaveRuleVO;

import java.util.List;

/**
 * Created by claire on 2019-07-10 - 15:09
 **/
public interface CacheService {
    void cacheAllRules(List<LeaveRuleVO> list);

    List<LeaveRuleVO> getRulesByKey(String key);
}
