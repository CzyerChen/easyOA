package easyoa.core.domain.dto;

import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by claire on 2019-06-27 - 15:13
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Excel("部门字典数据")
public class DeptExcel {
    //@Excel(name = "部门名称",width = 25)
    @ExcelField(value = "部门名称", required = true, maxLength = 50,
            comment = "提示：必填，长度不能超过50个字符")
    private String deptName;
    //@Excel(name = "所属中心",width = 25)
    @ExcelField(value = "所属中心", required = false, maxLength = 50,
            comment = "提示：必填，长度不能超过50个字符")
    private String center;
   // @Excel(name = "部门类型",width = 20)
   @ExcelField(value = "部门类型", required = false, maxLength = 20,
           comment = "提示：必填，长度不能超过20个字符")
    private String deptType;

}
