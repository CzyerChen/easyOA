package easyoa.rule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import easyoa.common.constant.AppConstant;
import easyoa.common.constant.Weekday;
import easyoa.common.model.api.ApiResult;
import easyoa.common.model.api.ResultData;
import easyoa.common.utils.DateUtil;
import easyoa.rulemanager.calendar.CalendarServer;
import easyoa.rulemanager.domain.GlobalVacation;
import easyoa.rulemanager.domain.DailyDetails;
import easyoa.rulemanager.model.api.BaseVacationModel;
import easyoa.rulemanager.model.api.FestivalDataModel;
import easyoa.rulemanager.model.api.ModelForRecentVacation;
import easyoa.rulemanager.model.api.ModelForYearVacation;
import easyoa.rulemanager.service.DailyDetailsService;
import easyoa.rulemanager.service.VacationService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by claire on 2019-07-08 - 17:05
 **/
public class RemoteApiTest extends AbstractApplicationTest {
    @Autowired
    private DailyDetailsService dailyDetailsService;
    @Autowired
    private CalendarServer calendarServer;
    @Autowired
    private VacationService vacationService;

    @Value("${app.juhe.key}")
    private String key;

    @Test
    public void testDailyInfo() {
        String info = "{\"reason\":\"Success\",\"result\":{\"data\":{\"avoid\":\"嫁娶.入宅.修造.动土.会亲友.破土.\",\"animalsYear\":\"猪\",\"weekday\":\"星期一\",\"suit\":\"祭祀.出行.\",\"lunarYear\":\"己亥年\",\"lunar\":\"八月十一\",\"year-month\":\"2019-9\",\"date\":\"2019-9-9\"}},\"error_code\":0}";
        ApiResult<DailyDetails> obj = (ApiResult<DailyDetails>) JSON.parseObject(info, new TypeReference<ApiResult<DailyDetails>>() {
        });
        DailyDetails entity = obj.getResult().getData();
        entity.setLunarInfo(entity.getLunarYear() + "|" + entity.getLunar());
        String weekday1 = entity.getWeekday();
        Weekday weekday = AppConstant.weekdayMap.get(weekday1);
        switch (weekday) {
            case SATURDAY:
                entity.setStatus(1);
                break;
            case SUNDAY:
                entity.setStatus(1);
                break;
            default:
                entity.setStatus(0);
        }
        dailyDetailsService.saveDailyInfo(entity);
    }

    @Test
    public void testGetDailyInfoWithRemoteApi() {
        String date = DateUtil.formatLocalDateWithHyphen(LocalDate.now());
        if (StringUtils.isNotBlank(date)) {
            String dailyInfo = calendarServer.getDailyInfo(date, key);
            if (StringUtils.isNotBlank(dailyInfo)) {
                DailyDetails entity = parseObject(dailyInfo);
                if (entity != null) {
                    //dailyDetailsService.saveDailyInfo(entity);

                }

            }
        }
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


    @Test
    public void testYearVacation() {
        String yearVacation = calendarServer.getYearVacation("2019", key);
        ApiResult<ModelForYearVacation> result = JSON.parseObject(yearVacation, new TypeReference<ApiResult<ModelForYearVacation>>() {
        });
        if (result != null) {

            List<BaseVacationModel> holiday_list = result.getResult().getData().getHoliday_list();
            List<GlobalVacation> list = new ArrayList<>();
            holiday_list.forEach(h -> {

                GlobalVacation vacations = vacationService.findVacationByName(h.getName(), h.getStartday());
                if (vacations == null) {
                    GlobalVacation g = new GlobalVacation();
                    g.setName(h.getName());
                    g.setStartDate(DateUtil.parseLocalDate(h.getStartday()));
                    g.setFinish(false);
                    list.add(g);
                }
            });

            vacationService.saveVacation(list);

        }
    }

    /**
     * {
     * "reason":"Success",
     * "result":{
     * "data":{
     * "year":"2019",
     * "year-month":"2019-9",
     * "holiday":"[{\"desc\":\"9月13日放假，与周末连休。\",\"festival\":\"2019-9-13\",\"list\":[{\"date\":\"2019-9-13\",\"status\":\"1\"},{\"date\":\"2019-9-14\",\"status\":\"1\"},{\"date\":\"2019-9-15\",\"status\":\"1\"}],\"list#num#\":3,\"name\":\"中秋节\",\"rest\":\"拼假建议：2019年9月9日（周一）~2019年9月12日（周四）请假4天，可拼9天中秋节小长假\"},{\"desc\":\"10月1日至10月7日放假，9月29日（星期日）、10月12日（星期六）上班。\",\"festival\":\"2019-10-1\",\"list\":[{\"date\":\"2019-10-1\",\"status\":\"1\"},{\"date\":\"2019-9-29\",\"status\":\"2\"},{\"date\":\"2019-10-2\",\"status\":\"1\"},{\"date\":\"2019-10-3\",\"status\":\"1\"},{\"date\":\"2019-10-4\",\"status\":\"1\"},{\"date\":\"2019-10-7\",\"status\":\"1\"},{\"date\":\"2019-10-12\",\"status\":\"2\"},{\"date\":\"2019-10-5\",\"status\":\"1\"},{\"date\":\"2019-10-6\",\"status\":\"1\"}],\"list#num#\":9,\"name\":\"国庆节\",\"rest\":\"拼假建议：10月8日（周二）~10月12日（周六）请5天假，可拼13天国庆节小长假。\"}]",
     * "holiday_array":[
     * {
     * "desc":"9月13日放假，与周末连休。",
     * "festival":"2019-9-13",
     * "list":[
     * {
     * "date":"2019-9-13",
     * "status":"1"
     * },
     * {
     * "date":"2019-9-14",
     * "status":"1"
     * },
     * {
     * "date":"2019-9-15",
     * "status":"1"
     * }
     * ],
     * "list#num#":3,
     * "name":"中秋节",
     * "rest":"拼假建议：2019年9月9日（周一）~2019年9月12日（周四）请假4天，可拼9天中秋节小长假",
     * "list_num":3
     * },
     * ....
     * ]
     * }* 	},
     * "error_code":0
     * }
     */
    @Test
    public void testRecentVacation() {
        String recentVacation = calendarServer.getRecentVacation(DateUtil.formatLocalDateWithHyphenForYearMonth(LocalDate.now()), key);
        ApiResult<ModelForRecentVacation> result = JSON.parseObject(recentVacation, new TypeReference<ApiResult<ModelForRecentVacation>>() {
        });
        if (result != null) {
            List<FestivalDataModel> data = result.getResult().getData().getHoliday_array();
            if (data != null) {
                data.forEach(d -> {
                    List<BaseVacationModel> list = d.getList();
                    if (list != null) {
                        String date = list.get(0).getDate();
                        String endDate = list.get(list.size() - 1).getDate();
                        StringBuilder sb = new StringBuilder("");
                        list.forEach(l -> {
                            sb.append(l.getDate());
                            sb.append("|");
                            sb.append(l.getStatus());
                            sb.append(",");
                        });
                        sb.deleteCharAt(sb.length() - 1);
                        String detail = sb.toString();

                        GlobalVacation vacation = vacationService.findVacationByName(d.getName(),  false);
                        if (vacation != null) {
                            vacation.setDescription(d.getDesc());
                            vacation.setAdvice(d.getRest());
                            vacation.setEndDate(DateUtil.parseLocalDate(endDate));
                            vacation.setDays(d.getList_num());
                            vacation.setFestival(DateUtil.parseLocalDate(d.getFestival()));
                            vacation.setDetail(detail);

                            vacation.setFinish(true);
                            vacationService.saveVacation(vacation);
                        } else {
                            //数据已存在，不需要操作
                        }
                    }
                });
            }
        }
    }


    @Test
    public void test1() {
        //获取节假日数据
        vacationService.getYearVacation("2019");
    }

    @Test
    public void test2() {
        //获取近期假期
        for (int i = 9; i <10; i++) {
            vacationService.getRecentHoliday("2019-" + i);
        }
    }

    @Test
    public void getDataFromJUHE() {
        LocalDate date = LocalDate.of(2019, 8, 25);
        //获取每天数据
        for (int i = 1; i < 20; i++) {
            String date1 = DateUtil.formatLocalDateWithHyphen(date);
            vacationService.getDailyInfo(date1);
            date = date.plusDays(1);
        }
    }

}