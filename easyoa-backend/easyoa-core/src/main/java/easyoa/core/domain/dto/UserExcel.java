package easyoa.core.domain.dto;

import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import easyoa.core.utils.TimeReadConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by claire on 2019-06-27 - 16:40
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Excel("用户详情字典")
public class UserExcel {
    @ExcelField(value = "序号", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String id;
    @ExcelField(value = "工号", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String userCode;
    @ExcelField(value = "中文姓名", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String chineseName;
    @ExcelField(value = "英文名", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String englishName;
    @ExcelField(value = "员工类别", required = false, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String type;
    @ExcelField(value = "所属中心", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String center;
    @ExcelField(value = "一级部门", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String deptName;
    @ExcelField(value = "岗位", required = false, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String position;
    @ExcelField(value = "联系方式", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String phone;
    @ExcelField(value = "Email(公司邮箱)", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String email;
    @ExcelField(value = "性别", required = true, maxLength = 2,
            comment = "提示：必填，长度不能超过2个字符")
    private String sex;
    @ExcelField(value = "婚姻状况", required = true, maxLength = 10,
            comment = "提示：必填，长度不能超过10个字符")
    private String marriage;
    @ExcelField(value = "入职日期", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符",
            readConverter = TimeReadConverter.class)
    private Date hireDate;
    @ExcelField(value = "参加工作时间", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符",
            readConverter = TimeReadConverter.class)
    private Date workDate;
    @ExcelField(value = "累计工龄", required = true, maxLength = 5,
            comment = "提示：必填，长度不能超过5个字符")
    private String workYear;
    @ExcelField(value = "出生日期", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符",
            readConverter = TimeReadConverter.class)
    private Date birthDate;
    @ExcelField(value = "年龄", required = true, maxLength = 2,
            comment = "提示：必填，长度不能超过2个字符")
    private String age;
    @ExcelField(value = "司龄", required = true, maxLength = 2,
            comment = "提示：必填，长度不能超过2个字符")
    private String workInCompony;
    @ExcelField(value = "现居住地址", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String address;

    @ExcelField(value = "直接汇报人", required = true, maxLength = 10,
            comment = "提示：必填，长度不能超过10个字符")
    private String reporter;

    transient private String comment;
}
