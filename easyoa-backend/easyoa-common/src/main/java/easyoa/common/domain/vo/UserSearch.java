package easyoa.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by claire on 2019-07-03 - 10:13
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearch {
    private String username;
    private Integer deptId;
    private String createTimeFrom;
    private String createTimeTo;
    private Integer companyId;
}
