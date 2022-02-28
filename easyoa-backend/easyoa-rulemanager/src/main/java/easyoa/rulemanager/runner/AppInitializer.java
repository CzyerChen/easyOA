package easyoa.rulemanager.runner;

import easyoa.rulemanager.service.CacheService;
import easyoa.rulemanager.service.LeaveRulesService;
import easyoa.rulemanager.utils.EntityMapper;
import easyoa.common.domain.vo.LeaveRuleVO;
import easyoa.rulemanager.domain.LeaveRules;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by claire on 2019-07-10 - 15:23
 **/
@Slf4j
@Component
public class AppInitializer implements CommandLineRunner {

    @Autowired
    private CacheService cacheService;
    @Autowired
    private LeaveRulesService leaveRulesService;

    @Override
    public void run(String... args) throws Exception {
        log.info("============开始缓存休假规则==========");
        cacheRules();
        log.info("============结束缓存休假规则==========");

    }

    private void cacheRules() {
        List<LeaveRules> list = leaveRulesService.findAll();
        List<LeaveRuleVO> leaveRuleVOS = EntityMapper.rules2VO(list);
        if (list != null) {
            cacheService.cacheAllRules(leaveRuleVOS);
        }
    }
}
