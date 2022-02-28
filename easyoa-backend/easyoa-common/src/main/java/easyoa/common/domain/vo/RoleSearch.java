package easyoa.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by claire on 2019-07-03 - 10:49
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleSearch {
    private String roleName;
    private String createTimeFrom;
    private String createTimeTo;
}
