/**
 * Author:   claire
 * Date:    2020-01-20 - 17:25
 * Description: 假期数据
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package easyoa.core.domain.dto;

import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 功能简述 <br/> 
 * 〈假期数据〉
 *
 * @author claire
 * @date 2020-01-20 - 17:25
 * @since 1.3.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Excel("假期表格数据")
public class VacationExcel {
    @ExcelField(value = "假期名称",required = true,width = 100)
    private String vacationName;
    @ExcelField(value = "假期日期",required = true,width = 100)
    private String vacationDate;
    @ExcelField(value = "假期开始日期",required = true,width = 100)
    private String vacationBeginDate;
    @ExcelField(value = "假期结束日期",required = true,width = 100)
    private String vacationEndDate;
    @ExcelField(value = "假期持续天数",required = true,width = 100)
    private Integer vacationDays;
    @ExcelField(value = "假期调休",required = false,width = 100)
    private String offVacation;
    @ExcelField(value = "备注",required = false,width = 100)
    private String comment;


}
