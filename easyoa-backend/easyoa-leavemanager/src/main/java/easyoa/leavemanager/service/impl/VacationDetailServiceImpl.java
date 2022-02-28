/**
 * Author:   claire
 * Date:    2020-01-21 - 11:22
 * Description: 休假细节接口实现类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package easyoa.leavemanager.service.impl;

import easyoa.leavemanager.service.VacationDetailService;
import easyoa.common.utils.DateUtil;
import easyoa.core.domain.po.GlobalVacationDetail;
import easyoa.core.repository.GlobalVacationDetailRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 功能简述 <br/>
 * 〈休假细节接口实现类〉
 *
 * @author claire
 * @date 2020-01-21 - 11:22
 * @since 1.3.0
 */
@Service
public class VacationDetailServiceImpl implements VacationDetailService {
    @Autowired
    private GlobalVacationDetailRepository globalVacationDetailRepository;

    @Override
    public void saveVacationDetails(Set<LocalDate> vacationDate) {
        List<GlobalVacationDetail> list = new ArrayList<>();
        Iterator<LocalDate> iterator = vacationDate.iterator();
        while(iterator.hasNext()){
            GlobalVacationDetail detail = new GlobalVacationDetail();
            LocalDate date = iterator.next();
            detail.setOffday(DateUtil.formatDate(date,DateUtil.DATE_PATTERN_WITH_HYPHEN));
            detail.setYear(date.getYear());
            list.add(detail);
        }
        if(CollectionUtils.isNotEmpty(list)){
            globalVacationDetailRepository.saveAll(list);
        }
    }

    @Override
    public List<GlobalVacationDetail> findAllVacations(Integer year) {
        return globalVacationDetailRepository.findByYear(year);
    }
}
