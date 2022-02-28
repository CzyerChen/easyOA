package easyoa.core.domain.dto;

import com.wuwenze.poi.annotation.ExcelField;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by claire on 2019-07-24 - 10:08
 **/
@Data
@AllArgsConstructor
public class UserExcelTemplate {
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
    @ExcelField(value = "岗位", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String position;
    @ExcelField(value = "联系方式", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String phone;
    @ExcelField(value = "Email", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String email;
    @ExcelField(value = "性别", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String ssex;
    @ExcelField(value = "婚姻状况", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String marriage;
    @ExcelField(value = "入职日期", required = false, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String hireDate;
    @ExcelField(value = "参加工作时间", required = false, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String workDate;
    @ExcelField(value = "累计工龄", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String workYear;
    @ExcelField(value = "出生日期", required = false, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String birthDate;
    @ExcelField(value = "年龄", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String age;
    @ExcelField(value = "司龄", required = true, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String workInCompony;
    @ExcelField(value = "现居住地址", required = false, maxLength = 25,
            comment = "提示：必填，长度不能超过25个字符")
    private String address;

}
