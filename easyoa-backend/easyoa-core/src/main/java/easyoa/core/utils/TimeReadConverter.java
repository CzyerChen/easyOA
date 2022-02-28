package easyoa.core.utils;

import com.wuwenze.poi.convert.ReadConverter;
import com.wuwenze.poi.exception.ExcelKitReadConverterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

import java.util.Date;

/**
 * Created by claire on 2019-07-23 - 18:08
 **/
@Slf4j
public class TimeReadConverter implements ReadConverter {
    @Override
    public Date convert(Object o) throws ExcelKitReadConverterException {
        try {
            if (null == o || StringUtils.isBlank(o.toString())) {
                return null;
            } else {
                return HSSFDateUtil.getJavaDate(Double.valueOf(String.valueOf(o)));
            }
        }catch (Exception e){
            log.error("时间转换异常", e);
            return null;
        }
    }
}
