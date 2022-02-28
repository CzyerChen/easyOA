package easyoa.common.constant;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * 全局常量配置
 * Created by claire on 2019-06-21 - 16:30
 **/
public class AppConstant {
    //三方API前缀
    public static final String API_PRIFIX = "/api";
    /**
     * token加密常量字符串
     */
    public static final String TOKEN_KEY_PREFIX = "fb.cache.token";

    /**
     * root tree node
     */
    public static final String ROOT_TREE_ID = "0";

    public static final Map<String, Weekday> weekdayMap = ImmutableMap.<String, Weekday>builder()
            .put("星期一", Weekday.MONDAY)
               .put("星期二", Weekday.TUESDAY)
               .put("星期三", Weekday.WEDNESDAY)
               .put("星期四",Weekday.THURSDAY)
               .put("星期五",Weekday.FRIDAY)
               .put("星期六",Weekday.SATURDAY)
               .put("星期日",Weekday.SUNDAY)
               .build();

}
