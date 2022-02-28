package easyoa.common.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by claire on 2019-08-07 - 09:07
 **/
@Data
public class VacationCalSearch implements Serializable {
    private String userCode;
    private String month;
    private Integer companyId;
    private List<Integer> userIds;
}
