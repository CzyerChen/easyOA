package easyoa.leavemanager.domain.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;

/**
 * Created by claire on 2019-08-06 - 15:16
 **/
@Data
@ExcelTarget("员工申请报表")
public class ApplicationExcel {
    @Excel(name = "申请序号",width = 25)
    private Long applicationId;
    @Excel(name = "工号",width = 25)
    private String userCode;
    @Excel(name = "姓名",width = 25)
    private String userName;
    @Excel(name = "部门名称",width = 25)
    private String deptName;
    @Excel(name = "申请类型",width = 25)
    private String leaveType;
    @Excel(name = "休假开始时间",width = 25)
    private String startTime;
    @Excel(name = "休假结束时间",width = 25)
    private String endTime;
    @Excel(name = "申请创建时间",width = 25)
    private String createTime;
    @Excel(name = "休假天数",width = 25)
    private Double days;
    @Excel(name = "去年年假抵消天数",width = 25)
    private Double previousDays;
    @Excel(name = "今年年假抵消天数",width = 25)
    private Double currentDays;
    @Excel(name = "休假原因",width = 50)
    private String leaveReason;
    @Excel(name = "备注",width = 25)
    private String remark;
    @Excel(name = "申请状态",width = 25)
    private String status;
    @Excel(name = "申请阶段",width = 25)
    private String stage;
}
