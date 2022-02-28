package easyoa.rulemanager.service.impl;

import easyoa.common.constant.CacheConstant;
import easyoa.common.domain.vo.LeaveRuleVO;
import easyoa.rulemanager.service.RedissonService;
import org.redisson.api.RListMultimap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by claire on 2019-07-10 - 15:19
 **/
@Service(value = "redissonService")
public class RedissonServiceImpl implements RedissonService {
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public RListMultimap<String, LeaveRuleVO> getRuleMaps() {
        return redissonClient.getListMultimap(CacheConstant.LEAVE_RULES_CACHE_NAME);
    }
}
