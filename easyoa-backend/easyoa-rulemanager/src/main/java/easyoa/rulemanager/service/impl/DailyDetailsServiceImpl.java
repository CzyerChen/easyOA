package easyoa.rulemanager.service.impl;

import easyoa.rulemanager.domain.DailyDetails;
import easyoa.rulemanager.service.DailyDetailsService;
import easyoa.common.constant.AppConstant;
import easyoa.common.constant.Weekday;
import easyoa.rulemanager.repository.DailyDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by claire on 2019-07-08 - 17:51
 **/
@Service(value = "dailyDetailsService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DailyDetailsServiceImpl implements DailyDetailsService {

    @Autowired
    private DailyDetailsRepository dailyDetailsRepository;

    @Override
    public void saveDailyInfo(DailyDetails details) {
        initDailyDetail(details);
        dailyDetailsRepository.save(details);
    }

    private void initDailyDetail(DailyDetails entity){
        entity.setLunarInfo(entity.getLunarYear() + "|"+entity.getLunar());
        Weekday weekday = AppConstant.weekdayMap.get( entity.getWeekday());
        switch(weekday){
            case SATURDAY:entity.setStatus(1);break;
            case SUNDAY:entity.setStatus(1);break;
            default:entity.setStatus(0);
        } }
}
