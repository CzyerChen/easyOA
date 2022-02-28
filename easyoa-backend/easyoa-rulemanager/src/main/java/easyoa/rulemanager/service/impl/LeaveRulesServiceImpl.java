package easyoa.rulemanager.service.impl;

import easyoa.rulemanager.domain.LeaveRules;
import easyoa.rulemanager.service.LeaveRulesService;
import easyoa.rulemanager.service.UserVacationService;
import easyoa.rulemanager.repository.LeaveRulesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by claire on 2019-07-10 - 15:15
 **/
@Service(value = "leaveRulesService")
public class LeaveRulesServiceImpl implements LeaveRulesService {
    @Autowired
    private LeaveRulesRepository leaveRulesRepository;
    @Autowired
    private UserVacationService userVacationService;

    @Override
    public List<LeaveRules> findAll() {
        return leaveRulesRepository.findAll();
    }

    @Override
    public List<LeaveRules> findAllRules(Integer companyId) {
        return leaveRulesRepository.findAll();
    }

    @Override
    public Page<LeaveRules> findPageRules(Pageable pageable,Integer companyId) {
        if(pageable != null){
            return leaveRulesRepository.findAll((Specification<LeaveRules>) (root, cq, cb) -> {
                List<Predicate> predicateList = new ArrayList<>();
                if (Objects.nonNull(companyId)) {
                    predicateList.add(cb.equal(root.get("companyId").as(Integer.class), companyId));
                }
                Predicate[] pre = new Predicate[predicateList.size()];
                pre = predicateList.toArray(pre);
                return cq.where(pre).getRestriction();
            },pageable);
        }
        return null;
    }

    @Override
    public void saveLeaveRule(LeaveRules leaveRules) {
        LeaveRules oldRule = leaveRulesRepository.findByRuleId(leaveRules.getRuleId());
        LeaveRules newRule = leaveRulesRepository.save(leaveRules);
        if(oldRule.getMaxPermitDay() != newRule.getMaxPermitDay()){
            userVacationService.updateUserVacationInfo(oldRule,newRule);
        }
    }

    @Override
    public void deleteByIds(List<Integer> rules) {
        List<LeaveRules> list = leaveRulesRepository.findAllById(rules);
        if(list.size() != 0){
            leaveRulesRepository.deleteAll(list);
        }
    }
}
