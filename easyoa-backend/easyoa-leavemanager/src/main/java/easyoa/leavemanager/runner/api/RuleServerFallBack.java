package easyoa.leavemanager.runner.api;

import easyoa.common.domain.PageRequestEntry;
import easyoa.common.domain.vo.LeaveRuleVO;
import easyoa.common.domain.vo.VacationCalSearch;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-07-10 - 18:23
 **/
@Slf4j
public class RuleServerFallBack implements RuleServer {

    @Override
    public String checkAppyInfo(String message) {
        log.error("请求失败");
        return null;
    }

    @Override
    public List<LeaveRuleVO> getRuleList(Integer companyId) {
        log.error("请求失败");
        return null;
    }

    @Override
    public Map<String, Object> getRulePage(PageRequestEntry entry,Integer companyId) {
        log.error("请求失败");
        return null;
    }

    @Override
    public void updateRules(LeaveRuleVO leaveRuleVO) {
        log.error("请求失败");
    }

    @Override
    public void saveRules(LeaveRuleVO leaveRuleVO) {
        log.error("请求失败");
    }

    @Override
    public void deleteRules(String ruleIds) {
        log.error("请求失败");
    }

    @Override
    public String checkForApply(String type, Long applicaitonId) {
        log.error("请求失败");
        return null;
    }

    @Override
    public void saveUserVacation(String userId) {
        log.error("请求失败");
    }

    @Override
    public void saveUserVacationCal(String userId) {

    }

    @Override
    public Boolean updateUserVacationCal(VacationCalSearch search) {
        return null;
    }
}
