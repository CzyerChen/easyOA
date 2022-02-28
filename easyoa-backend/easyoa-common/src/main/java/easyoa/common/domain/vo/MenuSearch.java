package easyoa.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by claire on 2019-07-04 - 09:09
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuSearch {
    private String menuName;
    private String createTimeFrom;
    private String createTimeTo;
    private String type;
}
