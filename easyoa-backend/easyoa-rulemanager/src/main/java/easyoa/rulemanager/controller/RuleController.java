package easyoa.rulemanager.controller;

import easyoa.rulemanager.domain.LeaveRules;
import easyoa.rulemanager.service.LeaveRulesService;
import easyoa.rulemanager.utils.EntityMapper;
import easyoa.common.domain.PageRequestEntry;
import easyoa.common.domain.vo.LeaveRuleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-07-11 - 16:19
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/rule")
public class RuleController extends AbstractController{
    @Autowired
    private LeaveRulesService leaveRulesService;

    @GetMapping
    public List<LeaveRuleVO>  getRuleList(Integer companyId){
        List<LeaveRules> allRules = leaveRulesService.findAllRules(companyId);
        return EntityMapper.rules2VO(allRules);
    }

    @GetMapping("page")
    public Map<String,Object> getRulePage(@RequestBody PageRequestEntry entry,@RequestParam(required = false) Integer companyId){
        PageRequest pageRequest = getPageRequest(entry);
        Page<LeaveRules> allRules = leaveRulesService.findPageRules(pageRequest,companyId);
        List<LeaveRuleVO> leaveRuleVOS = EntityMapper.rules2VO(allRules.getContent());
        PageImpl<LeaveRuleVO> voPage = new PageImpl<>(leaveRuleVOS, pageRequest, allRules.getTotalElements());
        return getMap(voPage);
    }

    @PutMapping
    public void updateRule(@Valid @RequestBody LeaveRuleVO leaveRuleVO){
        LeaveRules leaveRules = EntityMapper.vo2Rules(leaveRuleVO);
        if(leaveRules != null) {
            leaveRulesService.saveLeaveRule(leaveRules);
        }
    }

    //规则暂时不允许新增
    @PostMapping
    public void saveRule(@Valid @RequestBody LeaveRuleVO leaveRuleVO){
        LeaveRules leaveRules = EntityMapper.vo2Rules(leaveRuleVO);
        leaveRules.setCreateTime(LocalDate.now());
        leaveRules.setUpdateTime(leaveRules.getCreateTime());
        leaveRulesService.saveLeaveRule(leaveRules);
    }


    @DeleteMapping("{ruleIds}")
    public void deletRules(@NotBlank @PathVariable String ruleIds ){
        List<String> ids = Arrays.asList(ruleIds.split(","));
        if(ids.size() != 0){
            List<Integer> list = ids.stream().map(Integer::valueOf).distinct().collect(Collectors.toList());
            leaveRulesService.deleteByIds(list);
        }
    }
}
