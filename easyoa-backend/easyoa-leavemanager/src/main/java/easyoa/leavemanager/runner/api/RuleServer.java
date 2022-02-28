package easyoa.leavemanager.runner.api;

import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import easyoa.common.domain.PageRequestEntry;
import easyoa.common.domain.vo.LeaveRuleVO;
import easyoa.common.domain.vo.VacationCalSearch;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-07-10 - 18:22
 **/
@FeignClient(
        value = "rule-server",
        fallback = RuleServerFallBack.class,
        url = "http://rule:9090",
//        url ="http://127.0.0.1:9090",
        configuration = RuleServer.RuleFeignConfig.class)
public interface RuleServer {

    @RequestMapping(value = "/message/check", method = RequestMethod.POST, consumes = "application/json")
    String checkAppyInfo(@RequestBody String message);

    @RequestMapping(value = "/rule", method = RequestMethod.GET)
    List<LeaveRuleVO> getRuleList(Integer companyId);

    @RequestMapping(value = "/rule/page", method = RequestMethod.GET,consumes ="application/json" )
    Map<String,Object> getRulePage(@RequestBody PageRequestEntry entry,@RequestParam(required = false) Integer companyId);

    @Async("taskExecutor")
    @RequestMapping(value = "/rule", method = RequestMethod.PUT, consumes = "application/json")
    void updateRules(@RequestBody LeaveRuleVO leaveRuleVO);

    @Async("taskExecutor")
    @RequestMapping(value = "/rule", method = RequestMethod.POST, consumes = "application/json")
    void saveRules(@RequestBody LeaveRuleVO leaveRuleVO);

    @Async("taskExecutor")
    @RequestMapping(value = "/rule/{ruleIds}", method = RequestMethod.DELETE, consumes = "application/json")
    void deleteRules(@PathVariable(name = "ruleIds") String ruleIds);

    @RequestMapping(value = "/message",method = RequestMethod.GET)
    String checkForApply(@RequestParam(name = "type")String type,@RequestParam(name = "applicaitonId")Long applicaitonId);

    @Async("taskExecutor")
    @RequestMapping(value = "/vacation/user", method = RequestMethod.POST, consumes = "application/json")
    void saveUserVacation(@RequestBody String  userId);

    @Async("taskExecutor")
    @RequestMapping(value = "/vacation/user/cal", method = RequestMethod.POST, consumes = "application/json")
    void saveUserVacationCal(@RequestBody String  userId);

    @RequestMapping(value = "/vacation/cal", method = RequestMethod.POST, consumes = "application/json")
    Boolean updateUserVacationCal(@RequestBody VacationCalSearch vacationCalSearch);



    class RuleFeignConfig {
        @Bean("ruleFeignBuilder")
        @Scope("prototype")
        public Feign.Builder ruleFeignBuilder() {
            return Feign.builder()
                    .encoder(new JacksonEncoder())//
                    .decoder(new JacksonDecoder())
                    .options(new Request.Options(1000, 5000))
                    .retryer(new Retryer.Default(5000, 5000, 3));
        }

        //实例化fallback
        @Bean("ruleServerFallBack")
        public RuleServerFallBack rsfb() {
            return new RuleServerFallBack();
        }
    }
}
