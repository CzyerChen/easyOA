package easyoa.core.domain.dto;

import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by claire on 2019-07-26 - 09:54
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Excel("员工汇报关系模板")
public class ReportRelationshipExcel {
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
