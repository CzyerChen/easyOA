package easyoa.core.domain.dto;

import com.wuwenze.poi.annotation.ExcelField;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by claire on 2019-08-07 - 18:47
 **/
@Data
@AllArgsConstructor
public class UserReporterExcelTemplate {
    @ExcelField(value = "员工姓名",required = true,width = 100,comment = "提示：员工姓名必填")
    private String userName;
    @ExcelField(value = "员工编号",required = true,width = 100,comment = "提示：员工编号必填")
    private String userCode;
    @ExcelField(value = "汇报人姓名",required = false,width = 100,comment = "提示：汇报人姓名必填")
    private String reporterName;
    @ExcelField(value = "汇报人编号",required = false,width = 100,comment = "提示：汇报人编号必填")
    private String reporterCode;
    @ExcelField(value = "附加汇报人姓名",required = false,width = 100,comment = "提示：附加汇报人姓名必填")
    private String otherReporterName;
    @ExcelField(value = "附加汇报人编号",required = false,width = 100,comment = "提示：附加汇报人编号必填")
    private String otherReporterCode;
}
