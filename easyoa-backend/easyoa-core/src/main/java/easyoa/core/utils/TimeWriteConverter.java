package easyoa.core.utils;


import com.wuwenze.poi.convert.WriteConverter;
import com.wuwenze.poi.exception.ExcelKitWriteConverterException;
import easyoa.common.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Execl导出时间类型字段格式化
 */
@Slf4j
public class TimeWriteConverter implements WriteConverter {
    @Override
    public String convert(Object value) throws ExcelKitWriteConverterException {
        try {
            if (value == null)
                return "";
            else {
                return DateUtil.formatFullTime(DateUtil.format2LocalDateTime(value.toString()),DateUtil.BASE_PATTERN);
            }
        } catch (Exception e) {
            log.error("时间转换异常", e);
            return "";
        }
    }
}
