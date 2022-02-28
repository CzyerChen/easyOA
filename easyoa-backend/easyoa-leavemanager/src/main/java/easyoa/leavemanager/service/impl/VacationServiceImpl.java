package easyoa.leavemanager.service.impl;

import easyoa.leavemanager.service.VacationDetailService;
import easyoa.common.domain.vo.VacationCalendar;
import easyoa.common.domain.vo.VacationVO;
import easyoa.common.utils.DateUtil;
import easyoa.core.domain.dto.VacationExcel;
import easyoa.core.domain.po.GlobalVacation;
import easyoa.core.repository.GlobalVacationRepository;
import easyoa.core.service.VacationService;
import jodd.util.StringPool;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-07-09 - 11:47
 **/
@Service(value = "vacationService")
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class VacationServiceImpl  implements VacationService {
    @Autowired
    private GlobalVacationRepository globalVacationRepository;
    @Autowired
    private VacationDetailService vacationDetailService;

    @Override
    public List<GlobalVacation> findAll() {
        return globalVacationRepository.findByFinish(true);
    }

    @Override
    public Page<GlobalVacation> findAll(Pageable pageable) {
        if(pageable != null){
            return globalVacationRepository.findByYear(LocalDate.now().getYear(),pageable);
        }
        return null;
    }

    @Override
    public VacationVO findVacationById(Integer vacationId) {
        Optional<GlobalVacation> optionalGlobalVacation = globalVacationRepository.findById(vacationId);
        if(optionalGlobalVacation.isPresent()){
            GlobalVacation v = optionalGlobalVacation.get();
            return VacationVO.builder()
                    .name(v.getName())
                    .festival(v.getFestival().toString())
                    .days(v.getDays())
                    .advice(v.getAdvice())
                    .description(v.getDescription())
                    .startDate(v.getStartDate().toString())
                    .endDate(v.getEndDate().toString())
                    .detail(v.getDetail())
                    .build();
        }
        return null;
    }

    @Override
    @Transactional
    public void saveVacation(GlobalVacation globalVacation) {
      globalVacationRepository.save(globalVacation);
    }

    @Override
    public List<VacationCalendar> findMonthlyCalendarInfo(int year,int month) {
        LocalDate firstDay = DateUtil.getFirstDay(year, month);
        LocalDate endDay = DateUtil.getEndDay(year, month);
        List<VacationCalendar> result = new ArrayList<>();
        List<GlobalVacation> list = globalVacationRepository.findByFinishAndStartDateBetween(true, firstDay, endDay);
        if (list!= null){
            list.forEach(l ->{
                VacationCalendar calendar = new VacationCalendar();
                //calendar.setDate(l.getFestival().toString());
                calendar.setType(l.getDays() > 3?"error":"success");
                calendar.setContent(l.getName()+" "+l.getFestival()+" 放假"+l.getDays()+"天");
                result.add(calendar);
            });
        }
        return result;
    }

    @Override
    public VacationCalendar findCalendarInfo(int year, int month, int day) {
        LocalDate localDate = LocalDate.of(year, month, day);
        GlobalVacation vacation = globalVacationRepository.findByFinishAndStartDate(true, localDate);
        if(vacation!= null){
            return VacationCalendar.builder()
                    .type(vacation.getDays()>3?"error":"success")
                    .content(vacation.getName()+" "+vacation.getFestival()+" 放假"+vacation.getDays()+"天")
                    .build();
        }
        return null;
    }

    @Override
    public void processGlobalVacation(List<VacationExcel> vacations) {
        List<GlobalVacation> list = new ArrayList<>();
        Set<LocalDate> offVacationList = new HashSet<>();
        Set<LocalDate> vacationDateList = new HashSet<>();

        if(CollectionUtils.isNotEmpty(vacations)) {
            vacations.forEach(vacation ->{
                List<LocalDate> vacationList = new ArrayList<>();
                List<String> vacationStrList = new ArrayList<>();

                LocalDate vacationBeginDate = DateUtil.parseLocalDateWithAlias(vacation.getVacationBeginDate());
                LocalDate vacationEndDate = DateUtil.parseLocalDateWithAlias(vacation.getVacationEndDate());
                if(vacationBeginDate.isEqual(vacationEndDate)){
                    vacationList.add(vacationBeginDate);
                }else {
                    while (vacationBeginDate.isBefore(vacationEndDate)) {
                        vacationList.add(vacationBeginDate);
                        vacationStrList.add(DateUtil.formatDate(vacationBeginDate,DateUtil.DATE_PATTERN_WITH_HYPHEN));
                        vacationBeginDate = vacationBeginDate.plusDays(1);
                    }
                    vacationList.add(vacationEndDate);
                    vacationStrList.add(DateUtil.formatDate(vacationEndDate,DateUtil.DATE_PATTERN_WITH_HYPHEN));
                }

                if(StringUtils.isNotBlank(vacation.getOffVacation())){
                    List<LocalDate> offvacationList = Arrays.stream(vacation.getOffVacation().split(StringPool.COMMA)).map(DateUtil::parseLocalDateWithAlias).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(offvacationList)){
                        offVacationList.addAll(offvacationList);
                    }
                }

                if(CollectionUtils.isNotEmpty(vacationList)) {
                    vacationDateList.addAll(vacationList);

                    GlobalVacation globalVacation = new GlobalVacation();
                    globalVacation.setName(vacation.getVacationName());
                    globalVacation.setFestival(DateUtil.parseLocalDateWithAlias(vacation.getVacationDate()));
                    globalVacation.setStartDate(vacationList.get(0));
                    globalVacation.setEndDate(vacationList.get(vacationList.size()-1));
                    globalVacation.setDays(vacation.getVacationDays());
                    globalVacation.setDescription(vacation.getComment());
                    globalVacation.setDetail(Strings.join(vacationStrList,','));
                    globalVacation.setAdvice(vacation.getComment());
                    globalVacation.setYear(globalVacation.getFestival().getYear());
                    if(globalVacation.getStartDate().isBefore(LocalDate.now())) {
                        globalVacation.setFinish(Boolean.TRUE);
                    }else{
                        globalVacation.setFinish(Boolean.FALSE);
                    }
                    list.add(globalVacation);
                }
            });

            if(CollectionUtils.isNotEmpty(list)){
                globalVacationRepository.saveAll(list);
            }
        }

//        LocalDate yearCurrent = LocalDate.of(LocalDate.now().getYear() + 1, 1, 1);
//        LocalDate yearAfter = LocalDate.of(LocalDate.now().getYear() + 2, 1, 1);

        LocalDate yearCurrent = LocalDate.of(LocalDate.now().getYear() , 1, 1);
        LocalDate yearAfter = LocalDate.of(LocalDate.now().getYear() + 1, 1, 1);
        while (yearCurrent.isBefore(yearAfter)) {
            if(DateUtil.isWeekend(DateUtil.localDateTimeConvertToDate(yearCurrent.atStartOfDay()))) {
                if (!offVacationList.contains(yearCurrent)) {
                      vacationDateList.add(yearCurrent);
                }
            }
            yearCurrent = yearCurrent.plusDays(1);
        }

        if(CollectionUtils.isNotEmpty(vacationDateList)){
            vacationDetailService.saveVacationDetails(vacationDateList);
        }
    }
}
