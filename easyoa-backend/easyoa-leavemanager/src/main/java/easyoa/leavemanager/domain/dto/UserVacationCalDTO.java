/**
 * Author:   claire
 * Date:    2020-01-21 - 17:45
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package easyoa.leavemanager.domain.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 功能简述 <br/> 
 * 〈〉
 *
 * @author claire
 * @date 2020-01-21 - 17:45
 * @since 1.3.0
 */
@Data
@AllArgsConstructor
@ExcelTarget("userVacationCal")
public class UserVacationCalDTO implements Serializable{

        private Integer id;
        private Long userId;

        @Excel(name = "员工编号", width = 25)
        private String userCode;
        @Excel(name = "姓名", width = 25)
        private String userName;
        @Excel(name = "入职日期", width = 25)
        private String hireDate;
        @Excel(name = "年假", width = 25)
        private Double annual;
        @Excel(name = "有薪病假", width = 25)
        private Double sick;
        @Excel(name = "病假", width = 25)
        private Double sickNormal;
        @Excel(name = "婚假", width = 25)
        private Double marriage;
        @Excel(name = "陪产假", width = 25)
        private Double materPaternity;
        @Excel(name = "产检假", width = 25)
        private Double maternity4;
        @Excel(name = "丧假", width = 25)
        private Double funeral;
        @Excel(name = "事假", width = 25)
        private Double casual;
        @Excel(name = "应有年假", width = 25)
        private Double annualShould;
        @Excel(name = "折算后年假", width = 25)
        private Double annualCal;
        @Excel(name = "剩余年假", width = 25)
        private Double annualRest;
        private LocalDate calculateMonth;
        private LocalDateTime calculateTime;
        private Double restAnnualLeave;
}
