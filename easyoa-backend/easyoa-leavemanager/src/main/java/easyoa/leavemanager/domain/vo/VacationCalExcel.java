package easyoa.leavemanager.domain.vo;

import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;

/**
 * Created by claire on 2019-08-06 - 16:01
 **/
@Data
@ExcelTarget("vacationCalExcel")
public class VacationCalExcel {
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "员工编号", width = 25)
    private String userCode;
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "姓名", width = 25)
    private String userName;
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "入职日期", width = 25)
    private String hireDate;
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "年假", width = 25)
    private Double annual;
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "有薪病假", width = 25)
    private Double sick;
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "病假", width = 25)
    private Double sickNormal;
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "婚假", width = 25)
    private Double marriage;
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "陪产假", width = 25)
    private Double materPaternity;
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "产检假", width = 25)
    private Double maternity4;
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "丧假", width = 25)
    private Double funeral;
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "事假", width = 25)
    private Double casual;
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "应有年假", width = 25)
    private Double annualShould;
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "折算后年假", width = 25)
    private Double annualCal;
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "剩余年假", width = 25)
    private Double annualRest;

}
