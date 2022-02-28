package easyoa.rulemanager.service;

import easyoa.common.domain.vo.LeaveRuleVO;
import org.redisson.api.RListMultimap;

/**
 * Created by claire on 2019-07-10 - 15:18
 **/
public interface RedissonService {

    RListMultimap<String, LeaveRuleVO> getRuleMaps();
}
