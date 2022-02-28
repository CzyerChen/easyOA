package easyoa.rulemanager.service.impl;

import easyoa.rulemanager.service.CacheService;
import easyoa.common.domain.vo.LeaveRuleVO;
import easyoa.rulemanager.service.RedissonService;
import org.redisson.api.RList;
import org.redisson.api.RListMultimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by claire on 2019-07-10 - 15:10
 **/
@Service(value = "cacheService")
public class CacheServiceImpl implements CacheService {

     @Autowired
     private RedissonService redissonService;

    @Override
    public void cacheAllRules(List<LeaveRuleVO> list) {
        if(list != null && list.size()>0) {
            RListMultimap<String, LeaveRuleVO> ruleMaps = redissonService.getRuleMaps();
            if(ruleMaps != null){
                ruleMaps.clear();
            }

            list.forEach(l->{
                if(!ruleMaps.containsKey(l.getRuleName())){
                    RList<LeaveRuleVO> leaveRules = ruleMaps.get(l.getRuleName());
                    leaveRules.add(l);
                    ruleMaps.replaceValues(l.getCompanyId()+l.getRuleName(),leaveRules);
                }else{
                    ruleMaps.put(l.getCompanyId()+l.getRuleName(),l);
                }
            });
        }

    }

    @Override
    public List<LeaveRuleVO> getRulesByKey(String key) {
        RListMultimap<String, LeaveRuleVO> ruleMaps = redissonService.getRuleMaps();
        if(ruleMaps.containsKey(key)){
            return ruleMaps.get(key);
        }
        return null;
    }
}
