package easyoa.rulemanager.repository;

import easyoa.rulemanager.domain.LeaveRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by claire on 2019-07-10 - 15:13
 **/
public interface LeaveRulesRepository extends JpaRepository<LeaveRules,Integer>, JpaSpecificationExecutor<LeaveRules> {
    LeaveRules findByRuleId(Integer ruleId);

    List<LeaveRules> findByCompanyId(Integer companyId);
}
