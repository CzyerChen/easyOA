package easyoa.rulemanager.service;

import easyoa.rulemanager.domain.LeaveRules;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by claire on 2019-07-10 - 15:14
 **/
public interface LeaveRulesService {
    List<LeaveRules> findAll();
    List<LeaveRules> findAllRules(Integer companyId);

    Page<LeaveRules> findPageRules(Pageable pageable,Integer companyId);

    void saveLeaveRule(LeaveRules leaveRules);

    void deleteByIds(List<Integer> rules);
}
