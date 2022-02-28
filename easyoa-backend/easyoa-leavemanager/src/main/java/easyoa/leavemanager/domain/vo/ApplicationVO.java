package easyoa.leavemanager.domain.vo;

import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.Data;

/**
 * Created by claire on 2019-07-11 - 15:56
 **/
@Data
@Excel("员工申请报表")
public class ApplicationVO {
    @ExcelField(value = "申请序号",width = 50)
    private Long applicationId;
    @ExcelField(value = "部门ID",width = 50)
    private Integer deptId;
    @ExcelField(value = "部门名称",width = 120)
    private String deptName;
    @ExcelField(value = "用户ID",width = 50)
    private Long userId;
    @ExcelField(value = "用户名称",width = 200)
    private String userName;
    @ExcelField(value = "用户职称",width = 150)
    private String position;
    @ExcelField(value = "申请类型",width = 50)
    private String leaveType;
    @ExcelField(value = "休假开始时间",width = 120)
    private String startTime;
    @ExcelField(value = "休假结束时间",width = 120)
    private String endTime;
    @ExcelField(value = "申请创建时间",width = 120)
    private String createTime;
    @ExcelField(value = "休假天数",width = 50)
    private Double days;
    @ExcelField(value = "去年年假抵消天数",width = 50)
    private Double previousDays;
    @ExcelField(value = "今年年假抵消天数",width = 50)
    private Double currentDays;
    @ExcelField(value = "休假原因",width = 200)
    private String leaveReason;
    @ExcelField(value = "备注",width = 120)
    private String remark;
    @ExcelField(value = "申请状态",width = 100)
    private String status;
    @ExcelField(value = "申请阶段",width = 250)
    private String stage;

    //@ExcelField(value = "文件资料",width = 50)
    private String resources;
    //@ExcelField(value = "文件名称",width = 150)
    private String fileName;
}
