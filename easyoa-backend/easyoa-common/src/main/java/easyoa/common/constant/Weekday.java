package easyoa.common.constant;

/**
 * Created by claire on 2019-07-08 - 18:03
 **/
public enum Weekday {
    SUNDAY(0,"Sun.","星期日"),
    MONDAY(1,"Mon.","星期一"),
    TUESDAY(2,"Tues.","星期二"),
    WEDNESDAY(3,"Wed.","星期三"),
    THURSDAY(4,"Thur.","星期四"),
    FRIDAY(5,"Fri.","星期五"),
    SATURDAY(6,"Sat.","星期六");

    private int value;
    private String shortenName;
    private String chineseName;

    Weekday(Integer value , String shortenName, String chineseName ){
        this.value = value;
        this.shortenName = shortenName;
        this.chineseName = chineseName;
    }

    public String getShortenName() {
        return shortenName;
    }

    public String getChineseName() {
        return chineseName;
    }

    public int getValue() {
        return value;
    }

    public static Weekday getNextDay(Weekday nowDay){
        int nextDayValue = nowDay.value;

        if (++nextDayValue == 7){
            nextDayValue =0;
        }

        return getWeekdayByValue(nextDayValue);
    }

    public static Weekday getWeekdayByValue(int value) {
        for (Weekday c : Weekday.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return null;
    }


}
