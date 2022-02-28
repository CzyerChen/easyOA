package easyoa.core.domain.dto;

import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by claire on 2019-07-02 - 10:56
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Excel("用户头像字典")
public class ImageExcel {
    //@Excel(name = "文件名",width = 25)
    @ExcelField(value = "文件名", required = true, maxLength = 50,
            comment = "提示：必填，长度不能超过50个字符")
    private String imageName;
}
