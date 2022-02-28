package easyoa.rulemanager.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import easyoa.rulemanager.calendar.CalendarServer;
import easyoa.rulemanager.domain.DailyDetails;
import easyoa.rulemanager.domain.GlobalVacation;
import easyoa.rulemanager.service.DailyDetailsService;
import easyoa.common.model.api.ApiResult;
import easyoa.common.model.api.ResultData;
import easyoa.common.utils.DateUtil;
import easyoa.rulemanager.model.api.BaseVacationModel;
import easyoa.rulemanager.model.api.FestivalDataModel;
import easyoa.rulemanager.model.api.ModelForRecentVacation;
import easyoa.rulemanager.model.api.ModelForYearVacation;
import easyoa.rulemanager.repository.GlobalVacationRepository;
import easyoa.rulemanager.service.VacationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-07-08 - 16:05
 **/
@Slf4j
@Service(value = "vacationService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class VacationServiceImpl implements VacationService {

    @Autowired
    private CalendarServer calendarServer;
    @Autowired
    private DailyDetailsService dailyDetailsService;
    @Autowired
    private GlobalVacationRepository globalVacationRepository;

    @Value("${app.juhe.key}")
    private String key;


    @Override
    public void getDailyInfo(String date) {
        if (StringUtils.isNotBlank(key)) {
          /*  int year = LocalDate.now().getYear();
            String vacation = calendarServer.getYearVacation(String.valueOf(year), key);*/
            if (StringUtils.isNotBlank(date)) {
                String dailyInfo = calendarServer.getDailyInfo(date, key);
                if (StringUtils.isNotBlank(dailyInfo)) {
                    DailyDetails entity = parseObject(dailyInfo);
                    if (entity != null) {
                        dailyDetailsService.saveDailyInfo(entity);
                    }
                } else {
                    log.error("data from juhe is null ,please check!");
                }
            }
        } else {
            log.error("聚合key为空，请检查配置");
        }
    }

    @Override
    public void getYearVacation(String year) {
        if (StringUtils.isNotBlank(year)) {
            String yearVacation = calendarServer.getYearVacation(year, key);
            ApiResult<ModelForYearVacation> result = JSON.parseObject(yearVacation, new TypeReference<ApiResult<ModelForYearVacation>>() {
            });
            if (result != null) {

                List<BaseVacationModel> holiday_list = result.getResult().getData().getHoliday_list();
                List<GlobalVacation> list = new ArrayList<>();
                holiday_list.forEach(h -> {

                    GlobalVacation vacations = findVacationByName(h.getName(), h.getStartday());
                    if (vacations == null) {
                        GlobalVacation g = new GlobalVacation();
                        g.setName(h.getName());
                        g.setFestival(DateUtil.parseLocalDate(h.getStartday()));
                        g.setFinish(false);
                        list.add(g);
                    }
                });

                saveVacation(list);
            }
        }
    }

    @Override
    public void getRecentHoliday(String yearMonth) {
        String recentVacation = calendarServer.getRecentVacation(yearMonth, key);
        ApiResult<ModelForRecentVacation> result = JSON.parseObject(recentVacation, new TypeReference<ApiResult<ModelForRecentVacation>>() {
        });
        if (result != null && result.getResult() != null
                && result.getResult().getData() != null
                && result.getResult().getData().getHoliday_array() != null) {
            List<FestivalDataModel> data = result.getResult().getData().getHoliday_array();
            if (data != null) {
                data.forEach(d -> {
                    List<BaseVacationModel> list = d.getList();
                    List<BaseVacationModel> models = list.stream().filter(l -> "1".equals(l.getStatus())).collect(Collectors.toList());
                    if (models != null && models.size() != 0) {
                        models.forEach(l -> l.setRealDate(DateUtil.parseLocalDate(l.getDate())));
                        List<BaseVacationModel> list1 = models.stream().sorted(Comparator.comparing(BaseVacationModel::getRealDate)).collect(Collectors.toList());
                        if (list1 != null && list1.size() != 0) {
                            GlobalVacation vacation = findVacationByName(d.getName(), false);
                            if (vacation != null) {
                                String startDate = list1.get(0).getDate();
                                String endDate = list1.get(list1.size() - 1).getDate();
                                StringBuilder sb = new StringBuilder();
                                list1.forEach(l -> {
                                    sb.append(l.getDate());
                                    sb.append(",");
                                });
                                sb.deleteCharAt(sb.length() - 1);
                                String detail = sb.toString();


                                vacation.setDescription(d.getDesc());
                                vacation.setAdvice(d.getRest());
                                vacation.setStartDate(DateUtil.parseLocalDate(startDate));
                                vacation.setEndDate(DateUtil.parseLocalDate(endDate));
                                vacation.setDays(list1.size());
                                vacation.setFestival(DateUtil.parseLocalDate(d.getFestival()));
                                vacation.setDetail(detail);

                                vacation.setFinish(true);
                                saveVacation(vacation);
                            } else {
                                log.info("节假日信息已存在{}", d.getName());
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void saveVacation(List<GlobalVacation> list) {
        globalVacationRepository.saveAll(list);
    }

    @Override
    public void saveVacation(GlobalVacation vacation) {
        globalVacationRepository.save(vacation);
    }

    @Override
    public GlobalVacation findVacationByName(String name, String startDate) {
        LocalDate localDate = DateUtil.parseLocalDate(startDate);
        if (localDate != null) {
            List<GlobalVacation> list = globalVacationRepository.findByNameAndStartDate(name, localDate);
            if (list != null && list.size() != 0) {
                return list.get(0);
            }
        }
        return null;
    }

    @Override
    public GlobalVacation findVacationByName(String name, boolean finish) {

        List<GlobalVacation> list = globalVacationRepository.findByNameAndFinish(name, finish);
        if (list != null && list.size() != 0) {
            return list.get(0);

        }
        return null;
    }


    private DailyDetails parseObject(String dataString) {
        ApiResult<DailyDetails> result = JSON.parseObject(dataString, new TypeReference<ApiResult<DailyDetails>>() {
        });
        if (result != null) {
            ResultData<DailyDetails> data = result.getResult();
            if (data != null) {
                return data.getData();
            }
        }
        return null;
    }

}
