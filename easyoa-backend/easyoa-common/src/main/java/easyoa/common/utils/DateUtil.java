package easyoa.common.utils;

import easyoa.common.model.WeekData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 时间帮助类
 * Created by claire on 2019-06-24 - 14:23
 **/
public class DateUtil {

    public static final String BASE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String FULL_TIME_PATTERN = "yyyyMMddHHmmss";
    public static final String DEFAULT_DATE_PATTERN = "yyyy/MM/dd";
    public static final String DATE_PATTERN_WITH_HYPHEN = "yyyy-MM-dd";
    public static final String DATE_PATTERN_WITH_HYPHEN_1 = "yyyy-M-d";
    public static final String DATE_PATTERN_WITH_HYPHEN_2 = "yyyy-M";
    public static final String DATE_PATTERN_HOUR_MINUTE = "yyyy-MM-dd HH:mm";
    public static final String DATE_PATTERN_MONTH = "yyyy-MM";

    /**
     * The ISO date formatter that formats or parses a date without an
     * * offset, such as '2011-12-03'.
     *
     * @param localDateTime
     * @return
     */
    public static String formatWithLocalDate(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * * The ISO date formatter that formats or parses a date without an
     * * offset, such as '20111203'.
     *
     * @param localDateTime
     * @return
     */
    public static String formatWithBasicIsoDate(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    /**
     * 指定pattern 格式化时间
     *
     * @param localDateTime
     * @param pattern
     * @return
     */
    public static String formatOfPattern(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 采用通用pattern 格式化时间
     *
     * @param localDateTime
     * @return
     */
    public static String format(LocalDateTime localDateTime) {
        return formatOfPattern(localDateTime, BASE_PATTERN);
    }


    /**
     * yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static Date parseDateWithHyphen(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN_WITH_HYPHEN);

        LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static String formatLocalDateWithHyphen(LocalDate date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN_WITH_HYPHEN_1);
        return dateTimeFormatter.format(date);
    }


    public static String formatLocalDateWithHyphenForYearMonth(LocalDate date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN_WITH_HYPHEN_2);
        return dateTimeFormatter.format(date);
    }

    /**
     * yyyy/MM/dd
     *
     * @param date
     * @return
     */
    public static Date parseDateWithDefaultPattern(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN);

        LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        return Date.from(zdt.toInstant());
    }


    //将java.util.Date 转换为java8 的java.time.LocalDateTime,默认时区为东8区
    public static LocalDateTime dateConvertToLocalDateTime(Date date) {
        return date.toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
    }

    public static LocalDate dateConvertToLocalDate(Date date) {
        return date.toInstant().atOffset(ZoneOffset.of("+8")).toLocalDate();
    }

    public static LocalDateTime format2LocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(BASE_PATTERN));
    }

    public static LocalDateTime format2LocalDateTimeWithPattern(String date, String pattern) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern));
    }


    //将java8 的 java.time.LocalDateTime 转换为 java.util.Date，默认时区为东8区
    public static Date localDateTimeConvertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.of("+8")));
    }

    public static String formatTimeStampWothBasePattern(long time) {
        Date date = new Date(time);
        LocalDateTime localDateTime = date.toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(BASE_PATTERN);
        return localDateTime.format(dateTimeFormatter);
    }


    public static String formatFullTime(LocalDateTime localDateTime) {
        return formatFullTime(localDateTime, FULL_TIME_PATTERN);
    }

    public static String formatFullTime(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(dateTimeFormatter);
    }

    public static String formatDate(LocalDate localDate, String pattern) {
        return localDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDate parseLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_PATTERN_WITH_HYPHEN_1));
    }

    public static LocalDate parseLocalDateWithAlias(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_PATTERN_WITH_HYPHEN));
    }

    public static LocalDate parseLocalDateWithPattern(String date,String pattern) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    public static Date parseDate(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_PATTERN_WITH_HYPHEN_1));
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static Date parseDateWithPattern(String date, String pattern) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static LocalDate getFirstDay(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return yearMonth.atDay(1);
    }

    public static LocalDate getEndDay(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return yearMonth.atEndOfMonth();
    }

    public static Double calculateDays(LocalDateTime start, LocalDateTime end) {
        LocalDateTime realStart = null;
        LocalDateTime realEnd = null;
        if (start.isAfter(LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 8, 29)) &&
                start.isBefore(LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 12, 01))) {
            realStart = LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 0, 0);
        } else if (start.isAfter(LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 12, 59)) &&
                start.isBefore(LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 17, 31))) {
            realStart = LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 12, 00);
        }
        if (end.isBefore(LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 12, 1)) &&
                end.isAfter(LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 8, 39))) {
            realEnd = LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 12, 0);
        } else if (end.isBefore(LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 17, 31)) &&
                end.isAfter(LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 12, 59))) {
            LocalDate localDate = LocalDate.of(end.getYear(), end.getMonth(), end.getDayOfMonth());
            realEnd = LocalDateTime.of(localDate.plusDays(1), LocalTime.MIN);
        }

        if (realStart != null && realEnd != null) {
            Duration between = Duration.between(realStart, realEnd);
            double days = between.toHours() * 1.0 / 24;
            if (days == 0) {
                return 0.5;
            }
            //不做周末的去除，始终以用户填写的时间为准
            /*while (realStart.isBefore(realEnd)) {
                Date date = Date.from(realStart.atZone(ZoneId.systemDefault()).toInstant());
                if (isWeekend(date)) {
                    days--;
                }
                realStart = realStart.plusDays(1);
            }*/
            return days;

        }
        return null;
    }

    public static boolean isWeekend(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        } else {
            return false;
        }
    }

    public static Map<Integer, WeekData> weeks(YearMonth yearMonth) {
        LocalDate start = LocalDate.now().with(yearMonth).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(yearMonth).with(TemporalAdjusters.lastDayOfMonth());

        return Stream.iterate(start, localDate -> localDate.plusDays(1L))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .collect(Collectors.groupingBy(localDate -> localDate.get(WeekFields.of(DayOfWeek.MONDAY, 1).weekOfMonth()),
                        Collectors.collectingAndThen(Collectors.toList(), WeekData::new)));
    }

    public static String formatCSTTime(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date d = sdf.parse(date);
        return DateUtil.getDateFormat(d, format);
    }

    private static String getDateFormat(Date date, String dateFormatType) {
        SimpleDateFormat simformat = new SimpleDateFormat(dateFormatType);
        return simformat.format(date);
    }

    public static List<String> daysFormatBetween(LocalDateTime from, LocalDateTime to, String format) {
        List<String> list = new ArrayList<>();
        while (to.isAfter(from) || to.equals(from)) {
            list.add(formatFullTime(from, format));
            from = from.plusDays(1);
        }
        return list;
    }

    public static void main(String[] args) {

       /* Date date = new Date(System.currentTimeMillis());
        LocalDateTime localDateTime = date.toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(BASE_PATTERN);
        String format = localDateTime.format(dateTimeFormatter);
        System.out.println(format);
*/
       /* Date date = DateUtil.parseDateWithDefaultPattern("2019/10/01");


        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(),ZoneId.systemDefault());

        dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));*/


       /* Long second = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));

        LocalDateTime dt = LocalDateTime.parse("2019-07-02 10:00:00", DateTimeFormatter.ofPattern(DateUtil.BASE_PATTERN));
        long l = dt.toEpochSecond(ZoneOffset.of("+8"));

        boolean res = second>l;*/

        //DateUtil.formatLocalDateWithHyphen(LocalDate.now());
        //LocalDate localDate = DateUtil.parseDate("2019-9-1");
        /*YearMonth yearMonth = YearMonth.of(2019, 9);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate andDay = yearMonth.atEndOfMonth();*/
        /*Date date = new Date(System.currentTimeMillis());
        int hours = date.getHours();
        int minutes = date.getMinutes();
        int seconds = date.getSeconds();*/

        /*Duration between = Duration.between(LocalDateTime.of(2019,7,19,12,0,0), LocalDateTime.of(2019,7,20,0,0,0));
        double days = between.toHours() * 1.0 / 24;
        System.out.println(days);*/

//        Double days = DateUtil.calculateDays(LocalDateTime.of(2019, 12, 28, 8, 30), LocalDateTime.of(2019, 12, 28, 17, 30));
//         System.out.println(days);
    }
}
