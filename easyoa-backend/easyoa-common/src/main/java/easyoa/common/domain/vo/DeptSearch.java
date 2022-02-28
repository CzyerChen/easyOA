package easyoa.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Created by claire on 2019-07-04 - 10:13
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptSearch {
    private String deptName;
    private Integer orderNum;
    private String createTimeFrom;
    private String createTimeTo;
    private Integer companyId;
}
